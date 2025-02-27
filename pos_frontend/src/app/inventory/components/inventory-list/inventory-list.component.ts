import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Inventory } from '../../../interfaces/inventory.interface';
import { InventoryService } from '../../../services/inventory.service';
import { ProductService } from '../../../services/product.service';
import { ToastService } from '../../../services/toast.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventory-list.component.html'
})
export class InventoryListComponent implements OnInit {
  @Input() inventory: Inventory[] = [];
  @Input() currentPage = 1;
  @Input() hasNextPage = false;
  @Input() loading = false;
  @Input() searchTerm = '';
  pageSize = 10;
  error: string | null = null;
  totalItems = 0;
  productMap = new Map<number, string>();
  
  @Output() pageChange = new EventEmitter<number>();
  @Output() searchChange = new EventEmitter<string>();
  @Output() addNew = new EventEmitter<void>();

  constructor(
    private inventoryService: InventoryService,
    private productService: ProductService,
    private toastService: ToastService,
    public authService: AuthService
  ) {}

  ngOnInit() {
    this.loadProductData();
    this.fetchInventory();
  }

  public fetchInventory() {
    this.loading = true;
    this.inventoryService.getInventory(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.inventory = response.data;
        this.totalItems = response.total;
        this.hasNextPage = response.hasNextPage;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching inventory:', error);
        this.error = error.error?.message || 'Failed to load inventory';
        this.loading = false;
        this.toastService.show('Failed to load inventory', 'error');
      }
    });
  }

  private loadProductData() {
    this.productService.getProducts(1, 1000).subscribe({
      next: (response) => {
        response.data.forEach((product: { id: number, name: string }) => {
          this.productMap.set(product.id, product.name);
        });
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.toastService.show('Failed to load product data', 'error');
      }
    });
  }

  getProductName(productId: number): string {
    return this.productMap.get(productId) || 'Unknown Product';
  }

  onPageChange(page: number) {
    this.pageChange.emit(page);
  }

  onSearchInput(term: string) {
    this.searchChange.emit(term);
  }

  onAddNew() {
    this.addNew.emit();
  }
} 