import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BookingListComponent } from './booking-list/booking-list.component';
import { BookingFormComponent } from './booking-form/booking-form.component';

const routes: Routes = [
  { path: '', component: BookingListComponent },
  { path: 'new', component: BookingFormComponent },
  { path: ':id/edit', component: BookingFormComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BookingsRoutingModule {}
