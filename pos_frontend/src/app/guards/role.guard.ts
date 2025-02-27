import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(role: string): boolean {
    if (this.authService.hasRole(role)) {
      return true;
    }
    
    this.router.navigate(['/unauthorized']);
    return false;
  }
} 