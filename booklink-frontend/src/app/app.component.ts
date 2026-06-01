import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './shared/services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'BookLink';

  constructor(public authService: AuthService, private router: Router) {}

  logout(): void {
    this.authService.logout().subscribe(() => this.router.navigate(['/login']));
  }
}
