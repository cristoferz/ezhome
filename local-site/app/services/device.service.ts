import { Injectable } from '@angular/core';
import { Headers, Http } from '@angular/http';

import 'rxjs/add/operator/toPromise';

import { DEVICES } from '../services/mock-devices';
import { Device } from '../classes/device';

@Injectable()
export class DeviceService {
    private _deviceUrl = 'http://localhost:8080';
    
    constructor (private http: Http) { }
    
    getDevices(): Promise<Device[]> {
        return this.http.get(this._deviceUrl+'/device/list')
        .toPromise()
        .then(response => response.json().devices)
        .catch(this.handleError);
    }
    
    connect(device: Device) {
        let headers = new Headers();
        headers.append('Content-Type', 'application/json');
        
        return this.http.post(this._deviceUrl+'/device/connect', JSON.stringify({ portName: device.name }), headers)
            .toPromise().then(() => device)
            .catch(this.handleError);
    }
    
    disconnect(device: Device) {
        let headers = new Headers();
        headers.append('Content-Type', 'application/json');
        
        return this.http.post(this._deviceUrl+'/device/disconnect', JSON.stringify({ portName: device.name }), headers)
            .toPromise().then(() => device)
            .catch(this.handleError);
    }
    
    private handleError(error: any) {
        console.log("teste ");
        console.error('An error occurred', error);
        return Promise.reject(error.message || error);
    }
    
}