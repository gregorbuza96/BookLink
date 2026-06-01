import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { BookingsRoutingModule } from './bookings-routing.module';
import { BookingListComponent } from './booking-list/booking-list.component';
import { BookingFormComponent } from './booking-form/booking-form.component';

@NgModule({
  declarations: [BookingListComponent, BookingFormComponent],
  imports: [CommonModule, ReactiveFormsModule, RouterModule, BookingsRoutingModule]
})
export class BookingsModule {}
