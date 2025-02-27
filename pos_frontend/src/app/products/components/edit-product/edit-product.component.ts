import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Product } from '../../../interfaces/product.interface';
import { ProductService } from '../../../services/product.service';
import { ToastService } from '../../../services/toast.service';

@Component({
  selector: 'edit-product',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-product.component.html'
})
export class EditProductComponent {
  @Input() product!: Product;
  @Output() cancel = new EventEmitter<void>();
  @Output() save = new EventEmitter<void>();

  constructor(
    private productService: ProductService,
    private toastService: ToastService
  ) {}

  onSubmit(form: NgForm) {
    if (form.valid) {
      this.productService.updateProduct(this.product.id, form.value)
        .subscribe({
          next: () => {
            this.toastService.show('Product updated successfully', 'success');
            this.save.emit();
          },
          error: (error) => {
            this.toastService.show(error.error?.message || 'Failed to update product', 'error');
          }
        });
    }
  }

  onCancel() {
    this.cancel.emit();
  }
} 