import {Component} from '@angular/core';

@Component({
    templateUrl: './app.invoice.component.html'
})
export class AppInvoiceComponent {

    print() {
        window.print();
    }
}
