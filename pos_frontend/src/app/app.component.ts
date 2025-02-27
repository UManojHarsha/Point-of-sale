import { Component } from '@angular/core';
import { RouterLink, RouterOutlet, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';
import { ToastComponent } from './components/toast/toast.component';
import { ToastService } from './services/toast.service';
import { FooterComponent } from './components/footer/footer.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule, ToastComponent, FooterComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'pos_frontend';
  isAuthenticated = false;
  userEmail?: string;
  userRole?: string;
  isSupervisor = false;

  constructor(
    private authService: AuthService, 
    private router: Router,
    private toastService: ToastService
  ) {
    this.authService.getAuthState().subscribe(state => {
      this.isAuthenticated = state.isAuthenticated;
      this.userEmail = state.userEmail;
      this.userRole = state.role;
      this.isSupervisor = state.role === 'ROLE_SUPERVISOR';
    });
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.toastService.show('Logged out successfully', 'success');
        setTimeout(() => {
          this.router.navigate(['/login'], { replaceUrl: true });
        }, 100);
      },
      error: (error) => {
        console.error('Logout error:', error);
        this.toastService.show('Logout failed, but session cleared', 'warning');
        setTimeout(() => {
          this.router.navigate(['/login'], { replaceUrl: true });
        }, 100);
      }
    });
  }
}
