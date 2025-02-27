import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  email: string = '';
  password: string = '';
  confirmPassword: string = '';
  error: string | null = null;
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastService: ToastService
  ) {}

  register() {
    if (!this.email || !this.password || !this.confirmPassword) {
      this.error = 'All fields are required';
      this.toastService.show('Please fill in all fields', 'warning');
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.error = 'Passwords do not match';
      this.toastService.show('Passwords do not match', 'warning');
      return;
    }

    this.loading = true;
    this.error = null;

    this.authService.register(this.email, this.password).subscribe({
      next: (response) => {
        this.toastService.show('Registration successful! Please log in', 'success');
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('Registration error:', error);
        this.error = error.error?.message || 'Registration failed';
        this.toastService.show('Registration failed: ' + this.error, 'error');
        this.loading = false;
      }
    });
  }
} 