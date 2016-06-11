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
var AppComponent = (function () {
    function AppComponent() {
        this.title = 'ezHome Test-Site';
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
        this.inputTypes = [
            {
                name: "Input",
                filter: "INPUT"
            },
            {
                name: "Output",
                filter: "OUTPUT"
            },
            {
                name: "Address",
                filter: "ADDRESS"
            }
        ];
        this.states = [
            { output: false, input: true, address: 1, internalAddress: false, state: false }, { output: false, input: true, address: 2, internalAddress: false, state: true }, { output: false, input: true, address: 3, internalAddress: false, state: true }, { output: false, input: true, address: 4, internalAddress: false, state: true }, { output: false, input: true, address: 5, internalAddress: false, state: true }, { output: false, input: true, address: 6, internalAddress: false, state: true }, { output: false, input: true, address: 7, internalAddress: false, state: true }, { output: false, input: true, address: 8, internalAddress: false, state: true }, { output: false, input: true, address: 9, internalAddress: false, state: true }, { output: false, input: true, address: 10, internalAddress: false, state: true }, { output: false, input: true, address: 12, internalAddress: false, state: true }, { output: false, input: true, address: 13, internalAddress: false, state: true }, { output: false, input: true, address: 14, internalAddress: false, state: false }, { output: true, input: false, address: 15, internalAddress: false, state: true }, { output: false, input: true, address: 16, internalAddress: false, state: true }, { output: false, input: true, address: 17, internalAddress: false, state: true }, { output: false, input: true, address: 18, internalAddress: false, state: true }, { output: false, input: true, address: 19, internalAddress: false, state: true }, { output: false, input: true, address: 20, internalAddress: false, state: true }, { output: false, input: true, address: 21, internalAddress: false, state: true }, { output: false, input: true, address: 22, internalAddress: false, state: true }, { output: false, input: true, address: 23, internalAddress: false, state: true }, { output: false, input: true, address: 24, internalAddress: false, state: true }, { output: false, input: true, address: 25, internalAddress: false, state: true }, { output: false, input: true, address: 26, internalAddress: false, state: true }, { output: false, input: true, address: 27, internalAddress: false, state: true }, { output: false, input: true, address: 28, internalAddress: false, state: true }, { output: false, input: true, address: 29, internalAddress: false, state: true }, { output: false, input: true, address: 30, internalAddress: false, state: true }, { output: false, input: true, address: 31, internalAddress: false, state: true }, { output: false, input: true, address: 32, internalAddress: false, state: true }, { output: false, input: true, address: 33, internalAddress: false, state: true }, { output: false, input: true, address: 34, internalAddress: false, state: true }, { output: false, input: true, address: 35, internalAddress: false, state: true }, { output: false, input: true, address: 36, internalAddress: false, state: true }, { output: false, input: true, address: 37, internalAddress: false, state: true }, { output: false, input: true, address: 38, internalAddress: false, state: true }, { output: false, input: true, address: 39, internalAddress: false, state: true }, { output: false, input: true, address: 40, internalAddress: false, state: true }, { output: false, input: true, address: 41, internalAddress: false, state: true }, { output: false, input: true, address: 42, internalAddress: false, state: true }, { output: false, input: true, address: 43, internalAddress: true, state: true }, { output: false, input: true, address: 44, internalAddress: false, state: true }, { output: false, input: true, address: 45, internalAddress: false, state: true }, { output: false, input: true, address: 46, internalAddress: false, state: true }, { output: false, input: true, address: 47, internalAddress: false, state: true }, { output: false, input: true, address: 48, internalAddress: false, state: true }, { output: false, input: true, address: 49, internalAddress: false, state: true }, { output: false, input: true, address: 50, internalAddress: false, state: true }, { output: false, input: true, address: 51, internalAddress: false, state: true }
        ];
    }
    AppComponent = __decorate([
        core_1.Component({
            selector: 'my-app',
            template: "\n    <md-sidenav-layout fullscreen>\n    <md-sidenav #sidenav >\n      <md-nav-list>\n        <a md-list-item *ngFor=\"let view of views\">\n          <spam md-line>{{view.name}}</spam>\n          <spam md-line>{{view.description}}</spam>\n        </a>\n      </md-nav-list>\n    </md-sidenav>\n    <md-toolbar color=\"primary\">\n   \n      <button md-icon-button (click)=\"sidenav.open()\">\n        <md-icon>menu</md-icon>\n      </button>\n      {{title}}\n    </md-toolbar>\n    <md-input placeholder=\"amount\" align=\"end\">\n      <span md-prefix>$&nbsp;</span>\n      <span md-suffix>.00</span>\n    </md-input>\n    <div class=\"card-container\" style=\"display:flex; flex-mode: row wrap\">\n      <md-card *ngFor=\"let type of inputTypes\" style=\"width: 150px; margin: 10px\">\n        <md-card-title>{{type.name}}</md-card-title>\n        <md-card-content>\n            <spam md-list-item *ngFor=\"#state of states\" >\n              <spam *ngIf=\"(type.filter == 'INPUT' && state.input) \n                        || (type.filter == 'OUTPUT' && state.output)\n                        || (type.filter == 'ADDRESS' && state.internalAddress)\" style=\"width: 50px\">\n                <md-checkbox [ngModel]=\"state.state\" style=\"width: 50px\">{{state.address}}</md-checkbox>\n              </spam>\n            </spam>\n          \n        </md-card-content>\n      </md-card>\n      \n    </div>\n  </md-sidenav-layout>\n  ",
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
                icon_1.MdIconRegistry
            ]
        }), 
        __metadata('design:paramtypes', [])
    ], AppComponent);
    return AppComponent;
}());
exports.AppComponent = AppComponent;
var MyFilterPipe = (function () {
    function MyFilterPipe() {
    }
    MyFilterPipe.prototype.transform = function (items, args) {
        // filter items array, items which match and return true will be kept, false will be filtered out
        return items.filter(function (item) { return item.output; });
    };
    MyFilterPipe = __decorate([
        core_1.Pipe({
            name: 'myfilter',
            pure: false
        }),
        core_1.Injectable(), 
        __metadata('design:paramtypes', [])
    ], MyFilterPipe);
    return MyFilterPipe;
}());
exports.MyFilterPipe = MyFilterPipe;
//# sourceMappingURL=app.component.js.map