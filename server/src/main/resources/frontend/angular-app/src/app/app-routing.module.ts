import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DeviceTableComponent } from './device-table/device-table.component';
import { DeviceDetailsComponent } from './device-details/device-details.component';

export const routes: Routes = [
  { path: 'device-table', component: DeviceTableComponent},
  { path: 'device-details/:id', component: DeviceDetailsComponent },
  { path: '',   redirectTo: '/device-table', pathMatch: 'full' }, // redirect to `first-component`
  { path: '**', component: DeviceTableComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {  }
