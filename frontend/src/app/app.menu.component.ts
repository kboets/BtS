import {Component, OnInit } from '@angular/core';


@Component({
    selector: 'app-menu',
    template: `
        <div class="menu">
            <ul class="layout-menu">
                <li app-menuitem *ngFor="let item of model; let i = index;" [item]="item" [index]="i" [root]="true"></li>
            </ul>
        </div>
    `
})
export class AppMenuComponent implements OnInit {

    model: any[];

    ngOnInit() {
        this.model = [
            {label: 'Leagues', icon: 'pi pi-fw pi-th-large', routerLink: ['/leagues']},
            {label: 'Result', icon: 'pi pi-fw pi-chart-line', routerLink: ['/result']},
            {label: 'Prospect', icon: 'pi pi-fw pi-cloud', routerLink: ['/prospect']},
            {label: 'Forecast', icon: 'pi pi-fw pi-money-bill', routerLink: ['/forecast']},
            {label: 'Admin', icon: 'pi pi-fw pi-wallet', routerLink: ['/admin']},
            {label: 'Dashboard', icon: 'pi c pi-home', routerLink: ['/']}
         ];
    }
}
