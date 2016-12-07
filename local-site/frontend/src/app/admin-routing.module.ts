import { NgModule } from '@angular/core';
import { AuthGuard } from './auth-guard.service';
import { RouterModule, Routes } from '@angular/router';

const adminRoutes: Routes = [
   // {
   //    path: 'admin',
   //    component: AdminComponent,
   //    canActivate: [AuthGuard],
   //    children: [
   //       {
   //          path: '',
   //          children: [
   //             { path: 'crises', component: ManageCrisesComponent },
   //             { path: 'heroes', component: ManageHeroesComponent },
   //             { path: '', component: AdminDashboardComponent }
   //          ],
   //       }
   //    ]
   // }
];

@NgModule({
   imports: [
      RouterModule.forChild(adminRoutes)
   ],
   exports: [
      RouterModule
   ]
})
export class AdminRoutingModule { }
