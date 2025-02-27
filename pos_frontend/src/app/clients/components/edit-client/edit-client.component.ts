import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Client } from '../../../interfaces/client.interface';
import { ClientService } from '../../../services/client.service';
import { ToastService } from '../../../services/toast.service';

@Component({
  selector: 'edit-client',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-client.component.html'
})
export class EditClientComponent implements OnInit {
  @Input() client!: Client;
  @Output() cancel = new EventEmitter<void>();
  @Output() save = new EventEmitter<void>();

  editingClient!: Client;

  constructor(
    private clientService: ClientService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.editingClient = {
      ...this.client,
      contactNo: this.client.contactNo
    };
  }

  onCancel() {
    this.cancel.emit();
  }

  onSubmit(form: NgForm) {
    if (form.valid) {
      this.clientService.updateClient(this.editingClient.id, this.editingClient)
        .subscribe({
          next: () => {
            this.toastService.show('Client updated successfully', 'success');
            this.save.emit();
          },
          error: (error) => {
            console.error('Error updating client:', error);
            this.toastService.show(error.error?.message || 'Failed to update client', 'error');
          }
        });
    } else {
      this.toastService.show('Please fill all required fields correctly', 'warning');
    }
  }
} 