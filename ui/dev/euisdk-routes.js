const express = require('express');
const path = require('path');
const fs = require('fs');

const containerRoot = path.join(__dirname, '../node_modules/@eui/container');
const clientRoot = path.join(__dirname, '../client');

/**
 * This function is used to give preference to my components over
 * components with the same name from E-UI SDK.
 *
 * @function applyOverride
 * @param {Array} override - array of my components
 * @param {Array} original - array of E-UI SDK components
 */
function applyOverride(override, original) {
  if (!override) {
    return;
  }
  override.forEach((component) => {
    const index = original.indexOf(component);
    if (index > -1) {
      original.splice(index, 1);
    }
  });
}

/**
 * Safely read the contents of the directory (components/panels/plugins)
 * and retrieve a list of components. If the directory is not found
 * it issues a warning to the console and moves on.
 *
 * @function readMyDirectory
 * @param {String} directoryPath - directory path in my client
 */
function readMyDirectory(directoryPath) {
  if (fs.existsSync(directoryPath)) {
    return fs.readdirSync(directoryPath);
  }
  console.warn(`${directoryPath} is not found, skipping...`);
  return null;
}

/**
 * Get a list of components, panels and plugins from the E-UI SDK Container
 *
 * @function getEUISDKComponents
 */
function getEUISDKComponents() {
  const euisdkComponents = fs.readdirSync(`${containerRoot}/components/`);
  const euisdkPanels = fs.readdirSync(`${containerRoot}/panels/`);
  const euisdkPlugins = fs.readdirSync(`${containerRoot}/plugins/`);

  return { euisdkComponents, euisdkPanels, euisdkPlugins };
}

/**
 * Get a list of my components, panels and plugins from my client
 *
 * @function getMyComponents
 */
function getMyComponents() {
  const myComponents = readMyDirectory(`${clientRoot}/components/`);
  const myPanels = readMyDirectory(`${clientRoot}/panels/`);
  const myPlugins = readMyDirectory(`${clientRoot}/plugins/`);

  return { myComponents, myPanels, myPlugins };
}

const { euisdkComponents, euisdkPanels, euisdkPlugins } = getEUISDKComponents();
const { myComponents, myPanels, myPlugins } = getMyComponents();

// Where conflicts exist client takes priority
applyOverride(myComponents, euisdkComponents);
applyOverride(myPanels, euisdkPanels);
applyOverride(myPlugins, euisdkPlugins);


function init(app) {
  /**
   * routing for components/panels/plugins provided by container lib
   * provides access to source maps for development
   */
  app.use('/:folder/:component', (req, res, next) => {
    const { folder, component } = req.params;

    function reRoute(fileName) {
      const { originalUrl } = req;
      if (originalUrl.indexOf(`${fileName}.js`) !== -1) {
        res.redirect(originalUrl.replace(`${fileName}.js`, `${fileName}.debug.js`));
      } else {
        express.static(`${containerRoot}/${folder}/${component}`)(req, res, next);
      }
    }

    if (folder === 'components' && euisdkComponents.indexOf(component) !== -1) {
      reRoute('Main');
    } else if (folder === 'panels' && euisdkPanels.indexOf(component) !== -1) {
      reRoute('Panel');
    } else if (folder === 'plugins' && euisdkPlugins.indexOf(component) !== -1) {
      reRoute('index');
    } else {
      next();
    }
  });

  /**
   * routing for my components
   */
  app.use('/components/:component/:version/:file', (req, res, next) => {
    const { component, file } = req.params;
    if (file === 'config.json') {
      express.static(path.join(__dirname, `../client/components/${component}/${file}`))(req, res, next);
    } else {
      next();
    }
  });

  /**
   * libs
   */
  app.use('/libs/system.js', express.static(`${containerRoot}/libs/system.js`));
  app.use('/libs/@eui/', (req, res, next) => {
    // Use original url to preserve query for cache breaking
    const { originalUrl } = req;

    // This enables sourcemaps to be available for E-UI SDK packages in dev environment
    if (originalUrl.indexOf('Main.js') !== -1) {
      res.redirect(originalUrl.replace('Main.js', 'Main.debug.js'));
    } else if (originalUrl.indexOf('umd.js') !== -1) {
      // all umd files pass through this including container
      res.redirect(originalUrl.replace('umd', 'umd.debug'));
    } else {
      express.static(path.join(__dirname, '../node_modules/@eui/'))(req, res, next);
    }
  });
  app.use('/libs/@eui/container/', express.static(`${containerRoot}/libs/@eui/container/`));

  /**
   * assets
   */
  app.use('/assets/fonts', express.static(path.join(__dirname, '../node_modules/@eui/theme/0/fonts')));
  app.use('/assets/css', express.static(`${containerRoot}/assets/css/`));
  app.use('/assets/icons', express.static(`${containerRoot}/assets/icons`));
  app.use('/assets/img', express.static(`${containerRoot}/assets/img`));
  app.use('/assets/favicon.ico', express.static(`${containerRoot}/assets/favicon.ico`));
}

module.exports = {
  init,
};
