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
var http_1 = require('@angular/http');
require('rxjs/add/operator/toPromise');
var DeviceService = (function () {
    function DeviceService(http) {
        this.http = http;
        this._deviceUrl = 'http://localhost:8080';
    }
    DeviceService.prototype.getDevices = function () {
        return this.http.get(this._deviceUrl + '/device/list')
            .toPromise()
            .then(function (response) { return response.json().devices; })
            .catch(this.handleError);
    };
    DeviceService.prototype.connect = function (device) {
        var headers = new http_1.Headers();
        headers.append('Content-Type', 'application/json');
        return this.http.post(this._deviceUrl + '/device/connect', JSON.stringify({ portName: device.name }), headers)
            .toPromise().then(function () { return device; })
            .catch(this.handleError);
    };
    DeviceService.prototype.disconnect = function (device) {
        var headers = new http_1.Headers();
        headers.append('Content-Type', 'application/json');
        return this.http.post(this._deviceUrl + '/device/disconnect', JSON.stringify({ portName: device.name }), headers)
            .toPromise().then(function () { return device; })
            .catch(this.handleError);
    };
    DeviceService.prototype.handleError = function (error) {
        console.log("teste ");
        console.error('An error occurred', error);
        return Promise.reject(error.message || error);
    };
    DeviceService = __decorate([
        core_1.Injectable(), 
        __metadata('design:paramtypes', [http_1.Http])
    ], DeviceService);
    return DeviceService;
}());
exports.DeviceService = DeviceService;
//# sourceMappingURL=device.service.js.map