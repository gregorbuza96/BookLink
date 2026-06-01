import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { HotelsRoutingModule } from './hotels-routing.module';
import { HotelListComponent } from './hotel-list/hotel-list.component';
import { HotelFormComponent } from './hotel-form/hotel-form.component';

@NgModule({
  declarations: [HotelListComponent, HotelFormComponent],
  imports: [CommonModule, ReactiveFormsModule, RouterModule, HotelsRoutingModule]
})
export class HotelsModule {}
