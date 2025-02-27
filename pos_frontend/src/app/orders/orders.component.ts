import { Component, ViewChild, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Order, OrderItemsData} from '../interfaces/order.interface';
import { OrderService } from '../services/order.service';
import { ClientService } from '../services/client.service';
import { ProductService } from '../services/product.service';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';
import { Client } from '../interfaces/client.interface';
import { Product } from '../interfaces/product.interface';
import { tap } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { OrderListComponent } from './components/order-list/order-list.component';
import { PlaceOrderComponent } from './components/place-order/place-order.component';
import { InvoiceComponent } from './components/invoice/invoice.component';

interface OrderItem {
  product_id: number;
  quantity: number;
  price: number;
  product_name: string;
  total: number;
}

interface NewOrder {
  user_id: number;
  items: OrderItem[];
}

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    OrderListComponent,
    PlaceOrderComponent,
    InvoiceComponent
  ],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss']
})
export class OrdersComponent implements OnInit {
  @ViewChild(OrderListComponent) private orderList!: OrderListComponent;
  
  protected readonly Array = Array;
  
  products: any[] = [];
  orders: Order[] = [];
  currentPage = 1;
  pageSize = 10;
  totalItems = 0;
  loading = false;
  error: string | null = null;
  hasNextPage = false;
  clientMap = new Map<number, string>();
  productMap = new Map<number, string>();
  costMap = new Map<number, number>();
  barcodeMap = new Map<number, string>();
  showOrderModal = false;
  orderItems: { product_id: number; quantity: number; price: number; product_name: string; total: number; }[] = [];
  currentItem = { product_id: 0, quantity: 0 };
  expandedOrders: { [key: number]: boolean } = {};
  //orderDetailsMap: { [key: number]: OrderDetail[] } = {};
  selectedInvoiceOrder: Order | null = null;
  productSearchTerm = '';
  searchedProducts: Product[] = [];
  private searchSubject = new Subject<string>();
  showPlaceOrderModal = false;
  searchTerm = '';

  constructor(
    private orderService: OrderService,
    private clientService: ClientService,
    private productService: ProductService,
    private authService: AuthService,
    private toastService: ToastService
  ) {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      this.searchProducts(term);
    });
  }

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.loading = true;
    this.error = null;

    this.orderService.getOrders(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.orders = response.data;
        this.hasNextPage = response.hasNextPage;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading orders:', error);
        this.error = 'Failed to load orders';
        this.loading = false;
      }
    });
  }

  onPageChange(page: number) {
    if (page !== this.currentPage && page > 0 && (!this.hasNextPage || page <= this.currentPage + 1)) {
      this.currentPage = page;
      this.loadOrders();
    }
  }

  getClientName(clientId: number): string {
    return this.clientMap.get(clientId) || `Unknown Client (${clientId})`;
  }

  getProductName(productId: number): string {
    return this.productMap.get(productId) || `Unknown Product (${productId})`;
  }

  getProductCost(productId: number): number {
    return this.costMap.get(productId) || 0;
  }

  getProductBarcode(productId: number): string {
    return this.barcodeMap.get(productId) || 'N/A';
  }

  // toggleOrderDetails(order: Order) {
  //   if (this.expandedOrders[order.id]) {
  //     // If already expanded, just collapse
  //     this.expandedOrders[order.id] = false;
  //   } else {
  //     // If not expanded, fetch details and product info
  //     this.orderService.getOrderDetails(order.id).subscribe({
  //       next: (details) => {
  //         this.orderDetailsMap[order.id] = details;
          
  //         // Fetch product details for each order item
  //         const productRequests = details.map(detail => 
  //           this.productService.getProduct(detail.product_id).pipe(
  //             tap(product => {
  //               this.productMap.set(product.id, product.name);
  //               this.costMap.set(product.id, product.price);
  //               this.barcodeMap.set(product.id, product.barcode);
  //             })
  //           )
  //         );

  //         // Wait for all product requests to complete
  //         forkJoin(productRequests).subscribe({
  //           next: () => {
  //             this.expandedOrders[order.id] = true;
  //           },
  //           error: (error) => {
  //             console.error('Error fetching product details:', error);
  //             this.toastService.show('Failed to load product details', 'error');
  //           }
  //         });
  //       },
  //       error: (error) => {
  //         console.error('Error fetching order details:', error);
  //         this.toastService.show('Failed to load order details', 'error');
  //       }
  //     });
  //   }
  // }

  addNewOrder() {
    this.showOrderModal = true;
    this.orderItems = [];
    this.currentItem = { product_id: 0, quantity: 0 };
    this.toastService.show('Opening new order form', 'info');
  }

  onProductSearch(term: string) {
    this.searchSubject.next(term);
  }

  private searchProducts(term: string) {
    if (!term.trim()) {
      this.searchedProducts = [];
      return;
    }

    // Filter products locally like in products page
    this.searchedProducts = this.products.filter(product => 
      product.name.toLowerCase().includes(term.toLowerCase())
    );
  }

  selectProduct(product: Product) {
    this.currentItem = {
      product_id: product.id,
      quantity: 1  // Set default quantity to 1
    };
    this.productSearchTerm = product.name;
    this.searchedProducts = [];
  }

  // addItem() {
  //   if (this.currentItem.product_id && this.currentItem.quantity > 0) {
  //     const selectedProduct = this.products.find(p => p.id === this.currentItem.product_id);
      
  //     if (selectedProduct) {
  //       this.orderItems.push({
  //         product_id: this.currentItem.product_id,
  //         quantity: this.currentItem.quantity,
  //         price: selectedProduct.price,
  //         product_name: selectedProduct.name,
  //         total: this.currentItem.quantity * selectedProduct.price
  //       });

  //       // Reset form
  //       this.currentItem = { product_id: 0, quantity: 1 };
  //       this.productSearchTerm = '';
  //       this.searchedProducts = [];
  //       this.toastService.show('Item added to order', 'success');
  //     }
  //   } else {
  //     this.toastService.show('Please select a product and quantity', 'warning');
  //   }
  // }

  removeItem(index: number) {
    this.orderItems.splice(index, 1);
    this.toastService.show('Item removed from order', 'info');
  }

  // submitOrder() {
  //   if (this.orderItems.length === 0) {
  //     this.toastService.show('Cannot submit empty order', 'warning');
  //     return;
  //   }

  //   const total_price = this.orderItems.reduce((sum, item) => sum + item.total, 0);

  //   // Format order items according to backend expectations
  //   const formattedItems = this.orderItems.map(item => ({
  //     product_id: item.product_id,
  //     quantity: item.quantity,
  //     price: item.price
  //   }));

  //   const order = {
  //     user_email: this.authService.getCurrentUserEmail(),
  //     total_price: total_price,
  //     orderItems: formattedItems,
  //   };

  //   this.orderService.createOrder(order).subscribe({
  //     next: (response) => {
  //       this.showOrderModal = false;
  //       this.orderItems = [];
  //       this.loadOrders();
  //       this.toastService.show('Order created successfully', 'success');
  //     },
  //     error: (error) => {
  //       console.error('Error creating order:', error);
  //       this.error = error.error?.message || 'Failed to create order';
  //       this.toastService.show(`Failed to create order: ${error.error?.message || error.message}`);
  //     }
  //   });
  // }

  cancelOrder() {
    this.showOrderModal = false;
    this.orderItems = [];
    this.toastService.show('Cancelled creating order', 'info');
  }

  closeInvoiceViewer() {
    this.selectedInvoiceOrder = null;
  }

  getInvoiceButtonText(status: string): string {
    switch (status) {
      case 'PENDING_INVOICE':
        return 'Generate Invoice';
      case 'INVOICE_GENERATED':
        return 'Download Invoice';
      case 'COMPLETED':
        return 'Invoice Downloaded';
      default:
        return 'Unknown Status';
    }
  }

  handleInvoiceAction(order: Order) {
    if (order.status === 'PENDING_INVOICE') {
      this.generateInvoice(order);
    } else if (order.status === 'INVOICE_GENERATED') {
      this.downloadInvoice(order);
    }
  }

  private generateInvoice(order: Order) {
    this.orderService.generateInvoice(order.id).subscribe({
      next: (response: string) => {
        this.toastService.show(response || 'Invoice generated successfully', 'success');
        this.loadOrders(); // Refresh to get updated status
      },
      error: (error) => {
        console.error('Error generating invoice:', error);
        this.toastService.show(error.error || 'Failed to generate invoice', 'error');
      }
    });
  }

  private downloadInvoice(order: Order) {
    this.orderService.downloadInvoice(order.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `invoice-${order.id}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
        this.toastService.show('Invoice downloaded successfully', 'success');
        this.loadOrders(); // Refresh to get updated status
      },
      error: (error) => {
        console.error('Error downloading invoice:', error);
        this.toastService.show('Failed to download invoice', 'error');
      }
    });
  }

  validateQuantity() {
    if (this.currentItem.quantity < 1) {
      this.currentItem.quantity = 1;
    }
  }

  getOrderTotal(): number {
    return this.orderItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  }

  onSearchInput(term: string) {
    this.searchTerm = term;
    this.currentPage = 1; // Reset to first page when searching
    this.loadOrders();
  }

  // onStatusChange(data: {id: number, status: string}) {
  //   this.orderService.updateOrderStatus(data.id, data.status).subscribe({
  //     next: () => {
  //       this.toastService.show('Order status updated', 'success');
  //       this.orderList.fetchOrders();
  //     },
  //     error: (error: Error) => {
  //       console.error('Error updating order status:', error);
  //       this.toastService.show('Failed to update order status', 'error');
  //     }
  //   });
  // }

  showPlaceOrder() {
    this.showPlaceOrderModal = true;
  }

  cancelPlaceOrder() {
    this.showPlaceOrderModal = false;
  }

  savePlaceOrder() {
    this.showPlaceOrderModal = false;
    this.orderList.fetchOrders();
  }
}
