/**
 * Integration tests for <e-app-view>
 */
import { expect } from 'chai';
import AppView from '../AppView';
import {
  inShadow,
  injectHTMLElement,
} from '../../../../../test/utils';

describe('AppView Application Tests', () => {
    let container;
    let inject;
    before(() => {
      container = document.body.appendChild(document.createElement('div'));
      inject = injectHTMLElement.bind(null, container);
      window.EUI = undefined; // stub out the locale
      AppView.register();
    });

    after(() => {
      document.body.removeChild(container);
    });

    describe('Basic application setup', () => {
      it('should create a new <e-app-view>', async () => {
        const appUnderTest = await inject('<e-app-view></e-app-view>');
        // check shadow DOM
        const headingTag = inShadow(appUnderTest, 'h1');
        expect(headingTag.textContent, '"Your app markup goes here" was not found').to.equal('Your app markup goes here');
      });
    });
});
