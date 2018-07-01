import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { MaterialModule } from '@angular/material';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app.routing.module';

import { IndexComponent } from './pages/index/index.component';
import { LoginComponent } from './pages/login/login.component';
import { ErrorComponent } from './pages/error/error.component';

import { InputTextModule, FieldsetModule, ButtonModule, MessagesModule } from 'primeng/primeng';
import { MaterialComponent } from './pages/material/material.component';



@NgModule({
   declarations: [
      AppComponent,
      IndexComponent,
      LoginComponent,
      ErrorComponent,
      MaterialComponent
   ],
   imports: [
      AppRoutingModule,
      BrowserModule,
      FormsModule,
      HttpModule,
      InputTextModule,
      FieldsetModule,
      ButtonModule,
      MessagesModule,
      MaterialModule.forRoot()
   ],
   providers: [],
   bootstrap: [AppComponent]
})
export class AppModule {

}
