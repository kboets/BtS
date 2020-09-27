import {Component, OnInit} from '@angular/core';
import {MenuItem} from 'primeng/primeng';

@Component({
    templateUrl: './panelsdemo.component.html',
    styles: [`
        :host ::ng-deep button {
            margin-right: .25em;
        }
    `]
})
export class PanelsDemoComponent implements OnInit {

    items: MenuItem[];

    ngOnInit() {
        this.items = [
            {label: 'Angular.io', icon: 'fa fa-link', url: 'http://angular.io'},
            {label: 'Documentation', icon: 'fa fa-book', routerLink: ['/documentation']}
        ];
    }
}
