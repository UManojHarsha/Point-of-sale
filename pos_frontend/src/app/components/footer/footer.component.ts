import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <footer class="footer mt-auto">
      <div class="container">
        <div class="d-flex justify-content-between align-items-center py-3">
          <span>© 2025 Next SCM Solutions. All rights reserved.</span>
          <a routerLink="/dashboard" class="text-decoration-none">
            <i class="bi bi-house-door-fill me-1"></i>
            Back to Dashboard
          </a>
        </div>
      </div>
    </footer>
  `,
  styles: [`
    .footer {
      background-color: #f8f9fa;
      border-top: 1px solid #dee2e6;
      font-size: 0.9rem;
      margin-top: auto;
    }

    a {
      color: #6c757d;
      &:hover {
        color: #495057;
      }
    }
  `]
})
export class FooterComponent {} 