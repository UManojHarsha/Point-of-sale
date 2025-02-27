import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  error: string | null = null;
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastService: ToastService
  ) {}

  login() {
    if (!this.email || !this.password) {
      this.error = 'Email and password are required';
      this.toastService.show('Please fill in all fields', 'warning');
      return;
    }

    this.loading = true;
    this.error = null;

    this.authService.login(this.email, this.password).subscribe({
      next: (response) => {
        if (response.email) {
          this.toastService.show('Login successful! Welcome back', 'success');
          this.router.navigate(['/dashboard']);
        } else {
          this.error = 'Invalid credentials. Please try again.';
          this.toastService.show('Invalid credentials', 'error');
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Login error:', error);
        this.error = 'Invalid credentials or unauthorized access.';
        this.toastService.show('Login failed: ' + (error.error?.message || 'Invalid credentials'), 'error');
        this.loading = false;
      }
    });
  }
} 