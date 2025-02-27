import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, catchError, throwError, finalize, shareReplay, take } from 'rxjs';
import { tap, map } from 'rxjs/operators';
import { LoginResponse, AuthState, RegisterResponse } from '../interfaces/auth.interface';
import { Router } from '@angular/router';
import { ToastService } from './toast.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:9000/api';
  private authState = new BehaviorSubject<AuthState>({
    isAuthenticated: false,
    isLoading: false
  });
  
  private authCheckInProgress: Observable<boolean> | null = null;

  private storeUserDetails(userDetails: AuthState): void {
    localStorage.setItem('userDetails', JSON.stringify({
      isAuthenticated: userDetails.isAuthenticated,
      userEmail: userDetails.userEmail,
      role: userDetails.role,
      id: userDetails.id
    }));
  }

  private clearUserDetails(): void {
    localStorage.removeItem('userDetails');
    localStorage.removeItem('token');
  }

  constructor(
    private http: HttpClient,
    private router: Router,
    private toastService: ToastService
  ) {
    const storedDetails = localStorage.getItem('userDetails');
    if (storedDetails) {
      this.authState.next({
        ...JSON.parse(storedDetails),
        isLoading: false
      });
    }
    this.checkAuthStatus().subscribe();
  }

  public checkAuthStatus(): Observable<boolean> {
    if (this.authCheckInProgress) {
      return this.authCheckInProgress;
    }

    this.authCheckInProgress = this.http.get<any>(`${this.baseUrl}/session/check`, { 
      withCredentials: true 
    }).pipe(
      map(response => {
        if (response && response.email) {
          const authState: AuthState = {
            isAuthenticated: true,
            userEmail: response.email,
            role: response.role,
            id: response.id,
            isLoading: false
          };
          this.authState.next(authState);
          this.storeUserDetails(authState);
          return true;
        }
        if (this.router.url !== '/login') {
          this.router.navigate(['/login'], { replaceUrl: true });
        }
        return false;
      }),
      catchError(() => {
        this.authState.next({ 
          isAuthenticated: false,
          isLoading: false 
        });
        if (this.router.url !== '/login') {
          this.router.navigate(['/login'], { replaceUrl: true });
        }
        return of(false);
      }),
      finalize(() => {
        this.authCheckInProgress = null;
      }),
      shareReplay(1),
      take(1)
    );

    return this.authCheckInProgress;
  }

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/session/login`, {
      email,
      password
    }, { withCredentials: true }).pipe(
      tap(response => {
        const authState = {
          isAuthenticated: true,
          userEmail: response.email,
          role: response.role,
          id: response.id,
          isLoading: false
        };
        this.authState.next(authState);
        this.storeUserDetails(authState);
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(`${this.baseUrl}/session/logout`, {}, { withCredentials: true }).pipe(
      tap(() => {
        const authState = {
          isAuthenticated: false,
          userEmail: undefined,
          role: undefined,
          id: undefined,
          isLoading: false
        };
        this.authState.next(authState);
        this.clearUserDetails();
      }),
      catchError(error => {
        const authState = {
          isAuthenticated: false,
          userEmail: undefined,
          role: undefined,
          id: undefined,
          isLoading: false
        };
        this.authState.next(authState);
        this.clearUserDetails();
        return throwError(() => error);
      })
    );
  }

  getAuthState(): Observable<AuthState> {
    return this.authState.asObservable();
  }

  isAuthenticated(): boolean {
    return this.authState.value.isAuthenticated;
  }

  getCurrentUserEmail(): string | null {
    return this.authState.value.userEmail || null;
  }

  getCurrentUserRole(): string | null {
    return this.authState.value.role || null;
  }

  hasRole(role: string): boolean {
    return this.getCurrentUserRole() === role;
  }

  isSupervisor(): boolean {
    return this.hasRole('SUPERVISOR');
  }

  isOperator(): boolean {
    return this.hasRole('OPERATOR');
  }

  register(email: string, password: string): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.baseUrl}/session/register`, {
      email,
      password
    }).pipe(
      map(response => ({
        success: true,
        message: 'Registration successful'
      })),
      catchError(error => {
        console.log(email, password);
        console.error('Registration error here:', error);
        return of({
          success: false,
          message: error.error?.message || 'Registration failed'
        });
      })
    );
  }
} 