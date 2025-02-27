import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
  menuItems = [
    { 
      route: '/clients', 
      icon: 'bi-people-fill',
      label: 'Clients' 
    },
    { 
      route: '/products', 
      icon: 'bi-box2-fill',
      label: 'Products' 
    },
    { 
      route: '/orders', 
      icon: 'bi-cart-check-fill',
      label: 'Orders' 
    },
    { 
      route: '/inventory', 
      icon: 'bi-boxes',
      label: 'Inventory' 
    },
    { 
      route: '/daily-sales', 
      icon: 'bi-graph-up-arrow',
      label: 'Daily Sales' 
    },
    { 
      route: '/reports', 
      icon: 'bi-file-earmark-bar-graph-fill',
      label: 'Reports' 
    }
  ];
} 