import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InventoryService } from '../../../services/inventory.service';
import { ToastService } from '../../../services/toast.service';
import { HttpEventType } from '@angular/common/http';

@Component({
  selector: 'app-import-inventory',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './import-inventory.component.html'
})
export class ImportInventoryComponent {
  @Output() cancel = new EventEmitter<void>();
  @Output() save = new EventEmitter<void>();
  
  loading = false;
  error: string | null = null;
  uploadProgress = 0;
  errors: string[] = [];

  constructor(
    private inventoryService: InventoryService,
    private toastService: ToastService
  ) {}

    uploadInventoryMasters(event: any) {
        const file = event.target.files[0];
        if (file) {
        this.loading = true;
        this.error = null;
        this.uploadProgress = 0;

        this.inventoryService.uploadInventoryMastersFile(file).subscribe({
            next: (event) => {
            if (event.type === HttpEventType.UploadProgress) {
                const progress = Math.round(100 * event.loaded / event.total);
                this.uploadProgress = progress;
            } else if (event.type === HttpEventType.Response) {
                this.loading = false;
                this.toastService.show('Products uploaded successfully', 'success');
                this.save.emit();
            }
            },
            error: (error) => {
            console.error('Error uploading inventory:', error);
            this.errors = error.error?.message?.split('\n') || ['Failed to upload products'];
            this.loading = false;
            this.toastService.show('Error uploading inventory', 'error');
            // Reset file input
            const fileInput = document.getElementById('tsvFile') as HTMLInputElement;
            if (fileInput) {
                fileInput.value = '';
            }
            }
        });
        }
    }

  onCancel() {
    this.cancel.emit();
  }

  downloadTemplate() {
    this.inventoryService.downloadTemplateTsv().subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'inventory_template.tsv';
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Error downloading template:', error);
        this.toastService.show('Failed to download template', 'error');
      }
    });
  }
} 