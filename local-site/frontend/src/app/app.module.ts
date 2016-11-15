import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';

import { InputTextModule, FieldsetModule, ButtonModule, MessagesModule } from 'primeng/primeng';

@NgModule({
   declarations: [
      AppComponent
   ],
   imports: [
      BrowserModule,
      FormsModule,
      HttpModule,
      InputTextModule,
      FieldsetModule,
      ButtonModule,
      MessagesModule
   ],
   providers: [],
   bootstrap: [AppComponent]
})
export class AppModule {

}
