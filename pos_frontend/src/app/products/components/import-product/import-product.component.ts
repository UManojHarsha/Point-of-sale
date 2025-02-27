import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../../services/product.service';
import { ToastService } from '../../../services/toast.service';
import { ProductForm } from '../../../interfaces/product-form.interface';
import { HttpEventType } from '@angular/common/http';

@Component({
  selector: 'app-import-product',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './import-product.component.html'
})
export class ImportProductComponent {
  @Output() cancel = new EventEmitter<void>();
  @Output() save = new EventEmitter<void>();
  
  loading = false;
  error: string | null = null;
  uploadProgress = 0;
  errors: string[] = [];

  constructor(
    private productService: ProductService,
    private toastService: ToastService
  ) {}

  onCancel() {
    this.cancel.emit();
  }

//   processTsvFile(file: File) {
//     const reader = new FileReader();
//     reader.onload = (e) => {
//       const text = e.target?.result as string;
//       const lines = text.split('\n');
//       const products: ProductForm[] = [];
      
//       // Skip header row and process each line
//       for (let i = 1; i < lines.length; i++) {
//         const line = lines[i].trim();
//         if (!line) continue;
        
//         const [name, barcode, price, client_id] = line.split('\t');
//         if (!name || !barcode || !price || !client_id) {
//           this.toastService.show('Invalid TSV format', 'error');
//           return;
//         }

//         products.push({
//           name,
//           barcode,
//           price: parseFloat(price),
//           clientName: clientName
//         });
//       }

//       if (products.length > 0) {
//         this.productService.addBulkProducts(products)
//           .subscribe({
//             next: () => {
//               this.toastService.show(`Successfully imported ${products.length} products`, 'success');
//               this.save.emit();
//             },
//             error: (error) => {
//               this.toastService.show('Failed to import products', 'error');
//               console.error('Error adding products:', error);
//             }
//           });
//       }
//     };
//     reader.readAsText(file);
//   }

//   onFileSelected(event: Event) {
//     const input = event.target as HTMLInputElement;
//     if (input.files?.length) {
//       const file = input.files[0];
//       if (file.name.endsWith('.tsv')) {
//         this.processTsvFile(file);
//       } else {
//         this.toastService.show('Please upload a .tsv file', 'error');
//       }
//     }
//   }

  uploadProductMasters(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.loading = true;
      this.error = null;
      this.uploadProgress = 0;

      this.productService.uploadProductMastersFile(file).subscribe({
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
          console.error('Error importing products:', error);
          this.errors = error.error?.message?.split('\n') || ['Failed to import products'];
          this.loading = false;
          this.toastService.show('Error uploading products', 'error');
          // Reset file input
          const fileInput = document.getElementById('tsvFile') as HTMLInputElement;
          if (fileInput) {
            fileInput.value = '';
          }
        }
      });
    }
  }

  downloadTemplate() {
    this.productService.downloadTemplateTsv().subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'products_template.tsv';
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