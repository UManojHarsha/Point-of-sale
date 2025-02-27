import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Product } from '../../../interfaces/product.interface';
import { ProductService } from '../../../services/product.service';
import { ClientService } from '../../../services/client.service';
import { ToastService } from '../../../services/toast.service';
import { AuthService } from '../../../services/auth.service';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-list.component.html'
})
export class ProductListComponent implements OnInit {
  @Input() products: Product[] = [];
  @Input() currentPage = 1;
  @Input() hasNextPage = false;
  @Input() loading = false;
  @Input() searchTerm = '';
  pageSize = 10;
  error: string | null = null;
  totalItems = 0;
  clientMap = new Map<number, string>();
  clientEmailMap = new Map<number, string>();
  
  @Output() edit = new EventEmitter<Product>();
  @Output() pageChange = new EventEmitter<number>();
  @Output() searchChange = new EventEmitter<string>();
  @Output() addNew = new EventEmitter<void>();

  private searchSubject = new Subject<string>();

  constructor(
    private productService: ProductService,
    private clientService: ClientService,
    private toastService: ToastService,
    public authService: AuthService
  ) {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      this.currentPage = 1;
      if (term) {
        this.searchProducts();
      } else {
        this.fetchProducts();
      }
    });
  }

  ngOnInit() {
    this.loadClientData();
    this.fetchProducts();
  }

  public fetchProducts() {
    this.loading = true;
    this.productService.getProducts(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.products = response.data;
        this.totalItems = response.total;
        this.hasNextPage = response.hasNextPage;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching products:', error);
        this.error = error.error?.message || 'Failed to load products';
        this.loading = false;
        this.toastService.show('Failed to load products', 'error');
      }
    });
  }

  private loadClientData() {
    this.clientService.getClients(1, 1000).subscribe({
      next: (response) => {
        response.data.forEach((client: { id: number, name: string, email: string }) => {
          this.clientMap.set(client.id, client.name);
          this.clientEmailMap.set(client.id, client.email);
        });
      },
      error: (error) => {
        console.error('Error loading clients:', error);
        this.toastService.show('Failed to load client data', 'error');
      }
    });
  }

  getClientName(clientId: number): string {
    return this.clientMap.get(clientId) || 'Unknown Client';
  }

  getClientEmail(clientId: number): string {
    return this.clientEmailMap.get(clientId) || 'No Email';
  }

  onEdit(product: Product) {
    this.edit.emit(product);
  }

  onPageChange(page: number) {
    this.currentPage = page;
    if (this.searchTerm) {
      this.searchProducts();
    } else {
      this.fetchProducts();
    }
  }

  onSearchInput(term: string) {
    this.searchTerm = term;
    this.searchSubject.next(term);
  }

  onAddNew() {
    this.addNew.emit();
  }

  private searchProducts() {
    this.loading = true;
    const searchValue = this.searchTerm.replace("%", "\\%").replace("_", "\\_");
    this.productService.searchProducts(searchValue, this.currentPage, this.pageSize)
      .subscribe({
        next: (response) => {
          this.products = response.data;
          this.totalItems = response.total;
          this.hasNextPage = response.hasNextPage;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error searching products:', error);
          this.error = 'Failed to search products';
          this.loading = false;
        }
      });
  }
} 