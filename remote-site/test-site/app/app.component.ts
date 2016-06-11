import { Component, Injectable, Pipe, PipeTransform } from '@angular/core';

import { MD_SIDENAV_DIRECTIVES } from '@angular2-material/sidenav';
import { MD_BUTTON_DIRECTIVES } from '@angular2-material/button';
import { MD_TOOLBAR_DIRECTIVES } from '@angular2-material/toolbar';
import { MdIcon, MdIconRegistry } from '@angular2-material/icon';
import { MD_LIST_DIRECTIVES } from '@angular2-material/list';
import { MD_CHECKBOX_DIRECTIVES } from '@angular2-material/checkbox';
import { MD_CARD_DIRECTIVES } from '@angular2-material/card';
import { MD_INPUT_DIRECTIVES } from '@angular2-material/input';

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
    <md-input placeholder="amount" align="end">
      <span md-prefix>$&nbsp;</span>
      <span md-suffix>.00</span>
    </md-input>
    <div class="card-container" style="display:flex; flex-mode: row wrap">
      <md-card *ngFor="let type of inputTypes" style="width: 150px; margin: 10px">
        <md-card-title>{{type.name}}</md-card-title>
        <md-card-content>
            <spam md-list-item *ngFor="#state of states" >
              <spam *ngIf="(type.filter == 'INPUT' && state.input) 
                        || (type.filter == 'OUTPUT' && state.output)
                        || (type.filter == 'ADDRESS' && state.internalAddress)" style="width: 50px">
                <md-checkbox [ngModel]="state.state" style="width: 50px">{{state.address}}</md-checkbox>
              </spam>
            </spam>
          
        </md-card-content>
      </md-card>
      
    </div>
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
  ]
})
export class AppComponent {
    title = 'ezHome Test-Site';
    
    filterargs: {input: "true"};
    
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
    
    inputTypes: Object[] = [
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
    
    states: Object[] = [
      {output:false,input:true,address:1,internalAddress:false,state:false},{output:false,input:true,address:2,internalAddress:false,state:true},{output:false,input:true,address:3,internalAddress:false,state:true},{output:false,input:true,address:4,internalAddress:false,state:true},{output:false,input:true,address:5,internalAddress:false,state:true},{output:false,input:true,address:6,internalAddress:false,state:true},{output:false,input:true,address:7,internalAddress:false,state:true},{output:false,input:true,address:8,internalAddress:false,state:true},{output:false,input:true,address:9,internalAddress:false,state:true},{output:false,input:true,address:10,internalAddress:false,state:true},{output:false,input:true,address:12,internalAddress:false,state:true},{output:false,input:true,address:13,internalAddress:false,state:true},{output:false,input:true,address:14,internalAddress:false,state:false},{output:true,input:false,address:15,internalAddress:false,state:true},{output:false,input:true,address:16,internalAddress:false,state:true},{output:false,input:true,address:17,internalAddress:false,state:true},{output:false,input:true,address:18,internalAddress:false,state:true},{output:false,input:true,address:19,internalAddress:false,state:true},{output:false,input:true,address:20,internalAddress:false,state:true},{output:false,input:true,address:21,internalAddress:false,state:true},{output:false,input:true,address:22,internalAddress:false,state:true},{output:false,input:true,address:23,internalAddress:false,state:true},{output:false,input:true,address:24,internalAddress:false,state:true},{output:false,input:true,address:25,internalAddress:false,state:true},{output:false,input:true,address:26,internalAddress:false,state:true},{output:false,input:true,address:27,internalAddress:false,state:true},{output:false,input:true,address:28,internalAddress:false,state:true},{output:false,input:true,address:29,internalAddress:false,state:true},{output:false,input:true,address:30,internalAddress:false,state:true},{output:false,input:true,address:31,internalAddress:false,state:true},{output:false,input:true,address:32,internalAddress:false,state:true},{output:false,input:true,address:33,internalAddress:false,state:true},{output:false,input:true,address:34,internalAddress:false,state:true},{output:false,input:true,address:35,internalAddress:false,state:true},{output:false,input:true,address:36,internalAddress:false,state:true},{output:false,input:true,address:37,internalAddress:false,state:true},{output:false,input:true,address:38,internalAddress:false,state:true},{output:false,input:true,address:39,internalAddress:false,state:true},{output:false,input:true,address:40,internalAddress:false,state:true},{output:false,input:true,address:41,internalAddress:false,state:true},{output:false,input:true,address:42,internalAddress:false,state:true},{output:false,input:true,address:43,internalAddress:true,state:true},{output:false,input:true,address:44,internalAddress:false,state:true},{output:false,input:true,address:45,internalAddress:false,state:true},{output:false,input:true,address:46,internalAddress:false,state:true},{output:false,input:true,address:47,internalAddress:false,state:true},{output:false,input:true,address:48,internalAddress:false,state:true},{output:false,input:true,address:49,internalAddress:false,state:true},{output:false,input:true,address:50,internalAddress:false,state:true},{output:false,input:true,address:51,internalAddress:false,state:true}
    ];
}

@Pipe({
    name: 'myfilter',
    pure: false
})
@Injectable()
export class MyFilterPipe implements PipeTransform {
    transform(items: any[], args: any[]): any {
        // filter items array, items which match and return true will be kept, false will be filtered out
        return items.filter(item => item.output);
    }
    
}