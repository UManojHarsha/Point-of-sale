import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ClientService } from '../../../services/client.service';
import { ToastService } from '../../../services/toast.service';

@Component({
  selector: 'app-add-client',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-client.component.html'
})
export class AddClientComponent {
  @Output() save = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  constructor(
    private clientService: ClientService,
    private toastService: ToastService
  ) {}

  onSubmit(form: NgForm) {
    if (form.valid) {
      this.clientService.addClient(form.value).subscribe({
        next: () => {
          this.toastService.show('Client added successfully', 'success');
          this.save.emit();
        },
        error: (error) => {
          console.error('Error adding client:', error);
          this.toastService.show(error.error?.message || 'Failed to add client', 'error')
        }
      });
    }
  }

  onCancel() {
    this.cancel.emit();
  }
} 
