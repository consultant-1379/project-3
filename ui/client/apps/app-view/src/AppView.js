/**
 * AppView is defined as
 * `<e-app-view>`
 *
 * Imperatively create application
 * @example
 * let app = new AppView();
 *
 * Declaratively create application
 * @example
 * <e-app-view></e-app-view>
 *
 * @extends {App}
 */
import { definition } from '@eui/component';
import { App, html } from '@eui/app';
import style from './appView.css';
import "@eui/table";
import '@eui/layout';
import render from 'lit-html';

const OKO_URL = "http://localhost:8080/api/v1";

const OKO_CONSTS = {
  ADD_APPLICATION_FLYOUT_ID: "add-application-flyout",
  ADD_APPLICATION_NAME_ID: "add-application-name-input",
  ADD_APPLICATION_PRODUCT_ID: "add-application-product-input",
  APPLICATION_TABLE_ID: "application-table",
  REPOSITORY_TABLE_ID: "repo-table",
  ADD_JOB_BUTTON_ID: "add-job-button",
  ADD_JOB_FLYOUT_ID: "add-job-flyout",
  ADD_JOB_JOB_NAME_INPUT: "add-job-name-input",
  ADD_JOB_GERRIT_URL_INPUT: "add-job-gerrit-url-input",
  JOB_DETAIL_VIEW_HEADER: "job-detail-view-header",
  ADD_JOB_SUBMIT_BUTTON_ID: "add-job-submit-button-id",
  JOB_SUGGESTIONS_DIV: "job-suggestions-div",
  BUILD_HISTORY_HEADER_ID: "build-history-header",
  BUILD_HISTORY_TABLE_ID: "build-history-able"
}

@definition('e-app-view', {
  style,
  props: {
    response: { attribute: false },
  },
})
export default class AppView extends App {
  constructor() {
    super();
    this.appTableColumns = [
      {title: "ID", attribute: "col1", sortable: true},
      {title: "Application Name", attribute: "col2", sortable: true},
      {title: "Product", attribute: "col3", sortable: true},
      {title: "-", attribute: "col4"}
    ];

    this.repoTableColumns = [
      {title: "ID", attribute: "id", sortable: true, resizable: true},
      {title: "Jenkins Job", attribute: "job", sortable: true, resizable: true},
      {title: "Build Count", attribute: "buildCount", sortable: true, resizable: true},
      {title: "Fail Count", attribute: "failCount", sortable: true, resizable: true},
      {title: "Alerts", attribute: "alerts", sortable: true, resizable: true},
      {title: "Last Built", attribute: "lastBuilt", sortable: true, resizable: true},
      {title: "-", attribute: "delete", resizable: true}
    ];

    this.buildTableColumns = [
      {title: "ID", attribute: "id", sortable: true, resizable: true},
      {title: "Result", attribute: "result", sortable: true, resizable: true},
      {title: "Build OS", attribute: "builtOn", sortable: true, resizable: true},
      {title: "Date", attribute: "date", sortable: true, resizable: true},
      {title: "Causes", attribute: "causes", sortable: true, resizable: true},
      {title: "Culprits", attribute: "culprits", sortable: true, resizable: true}
    ]
    window.setTimeout(() => {getApplicationData(this.getRootNode().querySelector("e-app-view"), null)}, 1000);
  }

  showApplicationFlyout(e){
    setAddApplicationFlyoutShow(this, true);
  }

  addApplication(){
    var rootShadow = this.getRootNode();
    var name = rootShadow.getElementById(OKO_CONSTS.ADD_APPLICATION_NAME_ID).value;
    var product = rootShadow.getElementById(OKO_CONSTS.ADD_APPLICATION_PRODUCT_ID).value;
    rootShadow.getElementById(OKO_CONSTS.ADD_APPLICATION_NAME_ID).value = "";
    rootShadow.getElementById(OKO_CONSTS.ADD_APPLICATION_PRODUCT_ID).value = "";
    var data = {"name" : name, "product" : product};
    var req = new XMLHttpRequest();
    req.open("POST", OKO_URL + "/application")
    req.setRequestHeader("Content-Type", "application/json");
    req.addEventListener("load", e =>{
      console.log(e);
      getApplicationData(rootShadow.host, e);
    })
    req.send(JSON.stringify(data));
    setAddApplicationFlyoutShow(this, false);
  }

  
  render() {
    const { EUI } = window;
    return html`
    <eui-layout-v0-tile tile-title="Application management pane">
    <div slot="content">
      <eui-layout-v0-flyout-panel panel-title="Add a new application" id=${OKO_CONSTS.ADD_APPLICATION_FLYOUT_ID}>
        <div slot="content">
          <eui-base-v0-text-field minlength="2" labeltext="Name of application (e.g. ActivityServiceUI)" fullwidth id=${OKO_CONSTS.ADD_APPLICATION_NAME_ID}></eui-base-v0-text-field>
          <eui-base-v0-text-field minlength="2" labeltext="Product application is part of (e.g. ENM)" fullwidth id=${OKO_CONSTS.ADD_APPLICATION_PRODUCT_ID}></eui-base-v0-text-field>
          <eui-base-v0-button primary @click=${this.addApplication}>Add application</eui-base-v0-button>
        </div>
      </eui-layout-v0-flyout-panel>
      <h2>Applications</h2>
      <p>(click a row to see application jobs)</p>
      <eui-table-v0 id=${OKO_CONSTS.APPLICATION_TABLE_ID} .columns=${this.appTableColumns} @row-selected=${this} @row-click=${this} @eui-table:sort=${this}></eui-table-v0>
      <br><eui-base-v0-button primary  @click=${this.showApplicationFlyout}>Add Application</eui-base-v0-button>
      <hr>
      <br>
      <h2 id=${OKO_CONSTS.JOB_DETAIL_VIEW_HEADER}>Click a row above to see job details</h2>
      <eui-layout-v0-flyout-panel panel-title="Add a job" id=${OKO_CONSTS.ADD_JOB_FLYOUT_ID}><div slot="content">
        <eui-base-v0-text-field minlength="2" labeltext="Jenkins Job Name (must be an existing jenkins job)" fullwidth id=${OKO_CONSTS.ADD_JOB_JOB_NAME_INPUT}></eui-base-v0-text-field>
        <eui-base-v0-text-field minlength="2" labeltext="Gerrit URL" fullwidth id=${OKO_CONSTS.ADD_JOB_GERRIT_URL_INPUT}></eui-base-v0-text-field><br>
        <eui-base-v0-button primary id=${OKO_CONSTS.ADD_JOB_SUBMIT_BUTTON_ID}>Add job</eui-base-v0-button>
      </div></eui-layout-v0-flyout-panel>
      <eui-base-v0-button primary disabled id=${OKO_CONSTS.ADD_JOB_BUTTON_ID}>Add job</eui-base-v0-button><br>
      <div slot="content">
      <eui-table-v0 id=${OKO_CONSTS.REPOSITORY_TABLE_ID} .columns=${this.repoTableColumns} @row-click=${jobTableRowClick}></eui-table-v0>
      </div>
      <h3>Suggested jobs:</h3>
      <div id=${OKO_CONSTS.JOB_SUGGESTIONS_DIV}></div>
      <hr>
      <h2 id=${OKO_CONSTS.BUILD_HISTORY_HEADER_ID}>Build history</h2>
      <div slot="content">
      <eui-table-v0 id=${OKO_CONSTS.BUILD_HISTORY_TABLE_ID} .columns=${this.buildTableColumns}><eui-table-v0>
      </div>
      </div></eui-layout-v0-tile>`;
  }

  handleEvent(event){//TODO
    console.log(event);
    if(event.type === "row-click"){
      this.handleRowClick(event.detail);
    }
  }

  handleRowClick(row){
    console.log(row);
    getJobData(this.getRootNode().querySelector("e-app-view"), row);
  }
}

function setAddApplicationFlyoutShow(caller, val) {
  var addAppFlyout = caller.getRootNode().getElementById(OKO_CONSTS.ADD_APPLICATION_FLYOUT_ID);
  addAppFlyout.show = val;
}

function getApplicationData(appView, cause){
  console.log(appView);
  var root = appView.shadowRoot;
  var req = new XMLHttpRequest();
  req.open("GET", OKO_URL + "/application");
  req.addEventListener("load", e => {
    console.log(root)
    var table = root.getElementById(OKO_CONSTS.APPLICATION_TABLE_ID);
    console.log(table);
    var data = JSON.parse(e.target.response);
    data = data.map(x => {
      var deleteButton = html`
        <eui-base-v0-button warning @click=${e => {deleteApplication(e, x, appView)}}>Delete</eui-base-v0-button>
      `
      return {col1: x.id, col2: x.name, col3: x.product, col4: deleteButton};
    });
    if(data.length === 0) data = [{col1: "No", col2: "Applications", col3: "Found", col4: ""}];
    table.data = data;
  });
  req.send();
}

function getJobData(appView, appRow){
  var appId = appRow.col1;
  var req = new XMLHttpRequest();
  req.addEventListener("load", e => {
    appView.shadowRoot.getElementById(OKO_CONSTS.BUILD_HISTORY_TABLE_ID).data = [];
    appView.shadowRoot.getElementById(OKO_CONSTS.BUILD_HISTORY_TABLE_ID).innerHTML = "Build history";
    var table = appView.shadowRoot.getElementById(OKO_CONSTS.REPOSITORY_TABLE_ID);
    var button = appView.shadowRoot.getElementById(OKO_CONSTS.ADD_JOB_BUTTON_ID);
    var data = JSON.parse(e.target.response);
    console.log(data);
    data = data.map( x => {
      var delButton = html`
        <eui-base-v0-button warning @click=${e => {deleteJob(x.repository.id, appView, appRow)}}>Delete</eui-base-v0-button>
      `;
      var alerts = html`
        ${x.alerts.map(alert => html`<eui-base-v0-pill severity="critical">${alert.name}</eui-base-v0-pill>`)}
      `;
      var jobLink = html`
        <a href="https://fem106-eiffel004.lmera.ericsson.se:8443/jenkins/job/${x.repository.jobName}" target="_blank">${x.repository.jobName}</a>
      `;
      return {id: x.repository.id, job: jobLink, buildCount: x.buildCount, failCount: x.failCount, alerts: alerts, lastBuilt: x.lastBuilt, delete: delButton}
    });
    if(data.length === 0) data = [{id: "No", job: "jenkins", buildCount: "jobs", failCount: "found", alerts: "-", lastBuilt: "-", delete: "-"}];
    console.log(data);
    table.data = data;
    var buttonClone = button.cloneNode(true);
    buttonClone.addEventListener("click", () => {
      showAddJobFlyout(appView, appRow);
    });
    buttonClone.disabled = false;
    button.parentNode.replaceChild(buttonClone, button);
    appView.shadowRoot.getElementById(OKO_CONSTS.JOB_DETAIL_VIEW_HEADER).innerHTML= appRow.col2 + " jobs";
  });
  req.open("GET", OKO_URL + "/application/" + appId + "/report");
  req.send();

  getJobSuggestions(appView, appRow);
}

function deleteApplication(e, app, appView){
  var req = new XMLHttpRequest();
  req.open("DELETE", OKO_URL + "/application/" + app.id);
  req.addEventListener("load", e => {
    getApplicationData(appView, null);
  });
  req.send();
}


function showAddJobFlyout(appView, appRow){
  var flyout = appView.shadowRoot.getElementById(OKO_CONSTS.ADD_JOB_FLYOUT_ID);
  flyout.show = true;
  var submitButton = appView.shadowRoot.getElementById(OKO_CONSTS.ADD_JOB_SUBMIT_BUTTON_ID);
  var buttonClone = submitButton.cloneNode(true);
  buttonClone.addEventListener("click", () => {
    addJob(appView, appRow);
  });
  submitButton.parentNode.replaceChild(buttonClone, submitButton);
}

function addJob(appView, appRow){
  console.log(appRow);
  var appId = appRow.col1;
  var name = appView.shadowRoot.getElementById(OKO_CONSTS.ADD_JOB_JOB_NAME_INPUT).value.trim();
  var gerritUrl = appView.shadowRoot.getElementById(OKO_CONSTS.ADD_JOB_GERRIT_URL_INPUT).value.trim();
  if(name.length < 2 || gerritUrl.length < 2){
    alert("Valid jenkins job name and gerrit repo url mandatory");
    return;
  }
  var data = {applicationId: appId, jobName: name, gerritUrl: gerritUrl};
  var req = new XMLHttpRequest();
  req.open("POST", OKO_URL + "/repository");
  req.setRequestHeader("Content-Type", "application/json");
  req.addEventListener("load", (e) => {
    if(e.target.status>= 400){
      var message = JSON.parse(e.target.response).message;
      alert("Add job failed: " + message);
      console.log(e);
      return;
    }
    appView.shadowRoot.getElementById(OKO_CONSTS.ADD_JOB_FLYOUT_ID).show = false;
    getJobData(appView, appRow);
  });
  req.addEventListener("error", e => {
    alert("Add job failed: " + e);
  });
  req.send(JSON.stringify(data));
}

function deleteJob(repoId, appView, appRow){
  var req = new XMLHttpRequest();
  req.open("DELETE", OKO_URL + "/repository/" + repoId);
  req.addEventListener("load", e => {
    getJobData(appView, appRow);
  });
  req.send();
}

function jobTableRowClick(e) {
  var repoId = e.detail.id;
  var repoName = e.detail.job.values[0];
  var appView = e.target.getRootNode();
  loadAndRenderBuildInfo(repoId, repoName, appView);
}

function jobTableSort(e) {

}

function loadAndRenderBuildInfo(repoId, repoName, appView){
  var table = appView.getElementById(OKO_CONSTS.BUILD_HISTORY_TABLE_ID);
  appView.getElementById(OKO_CONSTS.BUILD_HISTORY_HEADER_ID).innerHTML = "Fetching " + repoName + " build history..."
  var req = new XMLHttpRequest();
  var handleError = (e, msg) => {
    console.log(e);
    alert("Sorry, something went wrong. If you see the funky ducks, tell them " + msg);
    appView.getElementById(OKO_CONSTS.BUILD_HISTORY_HEADER_ID).innerHTML = "Build history"
  }
  req.open("GET", OKO_URL + "/repository/" + repoId + "/summary");
  req.addEventListener("load", e => {
    var res = e.target;
    if(res.status >= 400){
      handleError(e, "[ERR load-builds-load-failed]");
      return;
    }
    appView.getElementById(OKO_CONSTS.BUILD_HISTORY_HEADER_ID).innerHTML = repoName + " build history";
    var builds = JSON.parse(e.target.response).builds;
    console.log(builds);
    if(builds.length < 1){
      builds = [{id: "No", result: "build", builtOn: "history", date: "found", causes: "-", culprits: "-"}];
    } else {
//sort in reverse by build date. String compare works here because dates are formatted year-month-day-hour-min-second, which is a happy accident
      builds.sort( (first, second) => {
        return second.date.localeCompare(first.date);
      });
      builds = builds.map(build => {
        var idElem = html`<a href="${build.url}" target="_blank">${build.id}</a>`;
        var resElem = html`<eui-base-v0-pill severity="${ build.result === "SUCCESS" ? "cleared" : "critical"}">${build.result}</eui-base-v0-pill>`
        return {id: idElem, builtOn: build.builtOn, result: resElem, date: build.date, causes: build.causes.join(","), culprits: build.culprits.join(",")};
      });
    }
    table.data = builds;
  });
  req.addEventListener("error", e => handleError(e, "[ERR load-builds-err]"));
  req.send();
}

//TODO - this would be better as a dropdown menu
function getJobSuggestions(appView, appRow){
  var appName = appRow.col2;
  var req = new XMLHttpRequest();
  req.open("GET", OKO_URL + "/repository/suggestions?s=" + appName);
  req.addEventListener("load", e => {
    var div = appView.shadowRoot.getElementById(OKO_CONSTS.JOB_SUGGESTIONS_DIV);
    var suggestions = JSON.parse(e.target.response);
    console.log(suggestions);
    var list = null;
    if(suggestions.length < 1){
      list = "<b>No suggestions found for " + appName + "</b>";
    } else {
      list = "";
      suggestions.map( suggestion => {
        list += "<a href=\"https://fem106-eiffel004.lmera.ericsson.se:8443/jenkins/job/" + suggestion + "\" target=\"_blank\">" + suggestion + "</a>, ";
      });
      list = list.slice(0, list.length - 2);
    }
    div.innerHTML = list;
  });
  req.send();
}

/**
 * Register the component as e-app-view.
 * Registration can be done at a later time and with a different name
 * Uncomment the below line to register the App if used outside the container
 */
// AppView.register();
