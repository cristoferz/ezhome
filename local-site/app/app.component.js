"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var core_1 = require('@angular/core');
var sidenav_1 = require('@angular2-material/sidenav');
var button_1 = require('@angular2-material/button');
var toolbar_1 = require('@angular2-material/toolbar');
var icon_1 = require('@angular2-material/icon');
var list_1 = require('@angular2-material/list');
var checkbox_1 = require('@angular2-material/checkbox');
var card_1 = require('@angular2-material/card');
var input_1 = require('@angular2-material/input');
var device_service_1 = require('./services/device.service');
var AppComponent = (function () {
    function AppComponent(deviceService) {
        this.deviceService = deviceService;
        this.title = 'ezHome Local Configuration Site';
        this.views = [
            {
                name: "My account",
                description: "Your account data"
            },
            {
                name: "Devices",
                description: "Devices connected to ezHome"
            }
        ];
    }
    AppComponent.prototype.getDevices = function () {
        var _this = this;
        this.deviceService.getDevices().then(function (devices) { return _this.devices = devices; });
    };
    AppComponent.prototype.connect = function (device) {
        var _this = this;
        this.deviceService.connect(device).then(function () { return _this.getDevices(); });
    };
    AppComponent.prototype.disconnect = function (device) {
        var _this = this;
        this.deviceService.disconnect(device).then(function () { return _this.getDevices(); });
    };
    AppComponent.prototype.ngOnInit = function () {
        this.getDevices();
    };
    AppComponent = __decorate([
        core_1.Component({
            selector: 'my-app',
            template: "\n    <md-sidenav-layout fullscreen>\n    <md-sidenav #sidenav >\n      <md-nav-list>\n        <a md-list-item *ngFor=\"let view of views\">\n          <spam md-line>{{view.name}}</spam>\n          <spam md-line>{{view.description}}</spam>\n        </a>\n      </md-nav-list>\n    </md-sidenav>\n    <md-toolbar color=\"primary\">\n   \n      <button md-icon-button (click)=\"sidenav.open()\">\n        <md-icon>menu</md-icon>\n      </button>\n      {{title}}\n    </md-toolbar>\n    \n    <md-card *ngFor=\"let device of devices\">\n      <md-card-title>{{device.name}}</md-card-title>\n      <md-card-content>\n        <p>Connected: {{device.connected}}</p>\n        <p>RuntimeId: {{device.runtimeId}}</p>\n        <p>VersionId: {{device.versionId}}</p>\n      </md-card-content>\n      <md-card-actions align=\"end\">\n        <button md-button (click)=\"connect(device)\">Connect</button>\n        <button md-button (click)=\"disconnect(device)\">Disconnect</button>\n      </md-card-actions>\n    </md-card> \n    \n    <button md-raised-button (click)=\"getDevices()\">Refresh</button>\n  </md-sidenav-layout>\n  ",
            directives: [
                sidenav_1.MD_SIDENAV_DIRECTIVES,
                button_1.MD_BUTTON_DIRECTIVES,
                toolbar_1.MD_TOOLBAR_DIRECTIVES,
                list_1.MD_LIST_DIRECTIVES,
                checkbox_1.MD_CHECKBOX_DIRECTIVES,
                card_1.MD_CARD_DIRECTIVES,
                input_1.MD_INPUT_DIRECTIVES,
                icon_1.MdIcon
            ],
            providers: [
                icon_1.MdIconRegistry,
                device_service_1.DeviceService
            ]
        }), 
        __metadata('design:paramtypes', [device_service_1.DeviceService])
    ], AppComponent);
    return AppComponent;
}());
exports.AppComponent = AppComponent;
//# sourceMappingURL=app.component.js.map