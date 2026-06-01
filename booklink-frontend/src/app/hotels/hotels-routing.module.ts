import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HotelListComponent } from './hotel-list/hotel-list.component';
import { HotelFormComponent } from './hotel-form/hotel-form.component';

const routes: Routes = [
  { path: '', component: HotelListComponent },
  { path: 'new', component: HotelFormComponent },
  { path: ':id/edit', component: HotelFormComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HotelsRoutingModule {}
