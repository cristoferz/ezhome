import { Component, OnInit } from '@angular/core';

import { MD_SIDENAV_DIRECTIVES } from '@angular2-material/sidenav';
import { MD_BUTTON_DIRECTIVES } from '@angular2-material/button';
import { MD_TOOLBAR_DIRECTIVES } from '@angular2-material/toolbar';
import { MdIcon, MdIconRegistry } from '@angular2-material/icon';
import { MD_LIST_DIRECTIVES } from '@angular2-material/list';
import { MD_CHECKBOX_DIRECTIVES } from '@angular2-material/checkbox';
import { MD_CARD_DIRECTIVES } from '@angular2-material/card';
import { MD_INPUT_DIRECTIVES } from '@angular2-material/input';

import { DeviceService }  from './services/device.service';
import { Device }  from './classes/device';

@Component({
  selector: 'my-app',
  template: `
    <md-sidenav-layout fullscreen>
    <md-sidenav #sidenav >
      <md-nav-list>
        <a md-list-item *ngFor="let view of views">
          <spam md-line>{{view.name}}</spam>
          <spam md-line>{{view.description}}</spam>
        </a>
      </md-nav-list>
    </md-sidenav>
    <md-toolbar color="primary">
   
      <button md-icon-button (click)="sidenav.open()">
        <md-icon>menu</md-icon>
      </button>
      {{title}}
    </md-toolbar>
    
    <md-card *ngFor="let device of devices">
      <md-card-title>{{device.name}}</md-card-title>
      <md-card-content>
        <p>Connected: {{device.connected}}</p>
        <p>RuntimeId: {{device.runtimeId}}</p>
        <p>VersionId: {{device.versionId}}</p>
      </md-card-content>
      <md-card-actions align="end">
        <button md-button (click)="connect(device)">Connect</button>
        <button md-button (click)="disconnect(device)">Disconnect</button>
      </md-card-actions>
    </md-card> 
    
    <button md-raised-button (click)="getDevices()">Refresh</button>
  </md-sidenav-layout>
  `,
  directives: [ 
    MD_SIDENAV_DIRECTIVES, 
    MD_BUTTON_DIRECTIVES, 
    MD_TOOLBAR_DIRECTIVES, 
    MD_LIST_DIRECTIVES, 
    MD_CHECKBOX_DIRECTIVES,
    MD_CARD_DIRECTIVES,
    MD_INPUT_DIRECTIVES,
    MdIcon 
  ],
  providers: [
    MdIconRegistry
    , DeviceService
  ]
})
export class AppComponent implements OnInit {
  title = 'ezHome Local Configuration Site';
  devices: Device[];

	constructor(private deviceService: DeviceService) {
	}

  views: Object[] = [
    {
      name: "My account",
      description: "Your account data"
    },
    {
      name: "Devices",
      description: "Devices connected to ezHome"
    }
  ];
  
  getDevices() {
    this.deviceService.getDevices().then(devices => this.devices = devices);
  }
  
  connect(device: Device) {
    this.deviceService.connect(device).then(() => this.getDevices());
  }

  
  disconnect(device: Device) {
    this.deviceService.disconnect(device).then(() => this.getDevices());
  }
    
  ngOnInit() {
    this.getDevices();
  }
}