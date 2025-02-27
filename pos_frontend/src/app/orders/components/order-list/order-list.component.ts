import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Order, OrderStatus, OrderItemsData } from '../../../interfaces/order.interface';
import { OrderService } from '../../../services/order.service';
import { ClientService } from '../../../services/client.service';
import { ProductService } from '../../../services/product.service';
import { ToastService } from '../../../services/toast.service';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-list.component.html'
})
export class OrderListComponent implements OnInit {
  @Input() orders: Order[] = [];
  @Input() currentPage = 1;
  @Input() hasNextPage = false;
  @Input() loading = false;
  @Input() searchTerm = '';
  pageSize = 10;
  error: string | null = null;
  totalItems = 0;
  clientMap = new Map<number, string>();
  productMap = new Map<number, string>();
  
  @Output() pageChange = new EventEmitter<number>();
  @Output() searchChange = new EventEmitter<string>();
  @Output() addNew = new EventEmitter<void>();
  @Output() invoiceAction = new EventEmitter<Order>();
  @Output() viewInvoice = new EventEmitter<Order>();

  expandedOrders: { [key: number]: boolean } = {};
  orderDetails: { [key: number]: OrderItemsData[] } = {};

  constructor(
    private orderService: OrderService,
    private clientService: ClientService,
    private productService: ProductService,
    private toastService: ToastService
  ) {}

  ngOnInit() {
    this.loadClientData();
    //this.loadProductData();
    this.fetchOrders();
  }

  public fetchOrders() {
    this.loading = true;
    this.orderService.getOrders(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.orders = response.data;
        this.totalItems = response.total;
        this.hasNextPage = response.hasNextPage;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching orders:', error);
        this.error = error.error?.message || 'Failed to load orders';
        this.loading = false;
        this.toastService.show('Failed to load orders', 'error');
      }
    });
  }

  private loadClientData() {
    this.clientService.getClients(1, 1000).subscribe({
      next: (response) => {
        response.data.forEach((client: { id: number, name: string }) => {
          this.clientMap.set(client.id, client.name);
        });
      },
      error: (error) => {
        console.error('Error loading clients:', error);
        this.toastService.show('Failed to load client data', 'error');
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

  getClientName(clientId: number): string {
    return this.clientMap.get(clientId) || 'Unknown Client';
  }

  getProductName(productId: number): string {
    return this.productMap.get(productId) || 'Unknown Product';
  }

  getInvoiceButtonText(status: OrderStatus): string {
    switch (status) {
      case OrderStatus.PENDING:
        return 'Generate Invoice';
      case OrderStatus.PROCESSING:
        return 'Download Invoice';
      case OrderStatus.COMPLETED:
        return 'Invoice Downloaded';
      default:
        return 'N/A';
    }
  }

  onInvoiceAction(order: Order) {
    this.invoiceAction.emit(order);
  }

  onViewInvoice(order: Order) {
    this.viewInvoice.emit(order);
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

  toggleDetails(order: Order) {
    if (!this.expandedOrders[order.id]) {
      this.orderService.getOrderDetails(order.id).subscribe({
        next: (details) => {
          this.orderDetails[order.id] = details;
          this.expandedOrders[order.id] = true;
        },
        error: (error) => {
          console.error('Error loading order details:', error);
          this.toastService.show('Failed to load order details', 'error');
        }
      });
    } else {
      this.expandedOrders[order.id] = false;
    }
  }
} 