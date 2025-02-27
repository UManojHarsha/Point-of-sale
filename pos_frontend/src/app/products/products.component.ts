import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Product } from '../interfaces/product.interface';
import { ToastService } from '../services/toast.service';
import { ProductListComponent } from './components/product-list/product-list.component';
import { EditProductComponent } from './components/edit-product/edit-product.component';
import { ImportProductComponent } from './components/import-product/import-product.component';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ProductListComponent,
    EditProductComponent,
    ImportProductComponent
  ],
  templateUrl: './products.component.html'
})
export class ProductsComponent {
  @ViewChild(ProductListComponent) private productList!: ProductListComponent;
  
  products: Product[] = [];
  currentPage = 1;
  hasNextPage = false;
  loading = false;
  searchTerm = '';
  editingProduct: Product | null = null;
  showImportModal = false;

  constructor(private toastService: ToastService) {}

  onPageChange(page: number) {
    this.currentPage = page;
  }

  onSearchInput(term: string) {
    this.searchTerm = term;
  }

  startEdit(product: Product) {
    this.editingProduct = { ...product };
    this.toastService.show('Editing product: ' + product.name, 'info');
  }

  cancelEdit() {
    this.editingProduct = null;
    this.toastService.show('Cancelled editing product', 'info');
  }

  saveEdit() {
    this.editingProduct = null;
    this.productList.fetchProducts();
  }

  showImport() {
    this.showImportModal = true;
  }

  cancelImport() {
    this.showImportModal = false;
  }

  saveImport() {
    this.showImportModal = false;
    this.productList.fetchProducts();
  }
}
