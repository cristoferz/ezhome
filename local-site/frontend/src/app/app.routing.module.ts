import { NgModule } from '@angular/core';
import { AuthGuard } from './auth-guard.service';
import { RouterModule, Routes } from '@angular/router';

import { IndexComponent } from './pages/index/index.component';
import { LoginComponent } from './pages/login/login.component';
import { ErrorComponent } from './pages/error/error.component';
import { MaterialComponent } from './pages/material/material.component';

const appRoutes: Routes = [
   { path: '', component: IndexComponent, canLoad: [AuthGuard] },
   { path: 'login', component: LoginComponent, data: {title: "Login"} },
   { path: 'material', component: MaterialComponent, data: {title: "Material"} },
   { path: '**', component: ErrorComponent }
];

// const appRoutes: Routes = [
//   {
//     path: 'admin',
//     loadChildren: 'app/admin/admin.module#AdminModule',
//     canLoad: [AuthGuard]
//   }
// ];

@NgModule({
  imports: [
    RouterModule.forRoot(appRoutes)
  ],
  exports: [
    RouterModule
  ],
//   providers: [
//     CanDeactivateGuard
//   ]
})
export class AppRoutingModule {}
