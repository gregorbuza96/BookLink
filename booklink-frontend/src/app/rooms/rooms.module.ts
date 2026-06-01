import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { RoomsRoutingModule } from './rooms-routing.module';
import { RoomListComponent } from './room-list/room-list.component';
import { RoomFormComponent } from './room-form/room-form.component';

@NgModule({
  declarations: [RoomListComponent, RoomFormComponent],
  imports: [CommonModule, ReactiveFormsModule, RouterModule, RoomsRoutingModule]
})
export class RoomsModule {}
