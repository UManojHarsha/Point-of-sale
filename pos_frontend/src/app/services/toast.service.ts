import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error' | 'info' | 'warning';
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private nextId = 0;
  private toasts = new BehaviorSubject<Toast[]>([]);
  toasts$ = this.toasts.asObservable();

  constructor() {
    // Add subscription to verify toast updates
    this.toasts$.subscribe(toasts => {
      console.log('Current toasts:', toasts);
    });
  }

  show(message: string, type: Toast['type'] = 'info') {
    console.log('Showing toast:', { message, type });
    const id = this.nextId++;
    const toast = { id, message, type };
    this.toasts.next([...this.toasts.value, toast]);

    // Auto remove after 3 seconds
    setTimeout(() => this.remove(id), 3000);
  }

  remove(id: number) {
    console.log('Removing toast:', id);
    this.toasts.next(this.toasts.value.filter(t => t.id !== id));
  }
} 