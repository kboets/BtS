import {Component, OnInit} from '@angular/core';
import {EventService} from '../service/eventservice';
import {SelectItem} from 'primeng/api';
import {Product} from '../domain/product';
import {ProductService} from '../service/productservice';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import {AdminService} from "../../bts/admin/admin.service";
import {map} from "rxjs/operators";
import {AdminKeys} from "../../bts/domain/adminKeys";

@Component({
    templateUrl: './dashboard.component.html'
})
export class DashboardDemoComponent implements OnInit {

    cities: SelectItem[];

    products: Product[];

    chartData: any;

    events: any[];

    selectedCity: any;

    scheduleHeader: any;

    fullcalendarOptions: any;

    currentSeason: number;

    constructor(private productService: ProductService, private eventService: EventService, private adminService: AdminService) {
    }

    ngOnInit() {
        this.productService.getProducts().then(data => this.products = data);

        this.eventService.getEvents().then(events => {
            this.events = events;
        });

        this.cities = [];
        this.cities.push({label: 'Select City', value: null});
        this.cities.push({label: 'New York', value: {id: 1, name: 'New York', code: 'NY'}});
        this.cities.push({label: 'Rome', value: {id: 2, name: 'Rome', code: 'RM'}});
        this.cities.push({label: 'London', value: {id: 3, name: 'London', code: 'LDN'}});
        this.cities.push({label: 'Istanbul', value: {id: 4, name: 'Istanbul', code: 'IST'}});
        this.cities.push({label: 'Paris', value: {id: 5, name: 'Paris', code: 'PRS'}});

        this.chartData = {
            labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
            datasets: [
                {
                    label: 'First Dataset',
                    data: [65, 59, 80, 81, 56, 55, 40],
                    fill: false,
                    borderColor: '#007be5'
                },
                {
                    label: 'Second Dataset',
                    data: [28, 48, 40, 19, 86, 27, 90],
                    fill: false,
                    borderColor: '#20d077'
                }
            ]
        };

        this.scheduleHeader = {
            left: 'prev,next today',
            center: 'title',
            right: 'month,agendaWeek,agendaDay'
        };

        this.fullcalendarOptions = {
            plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
            defaultDate: '2017-02-12',
            header: {
                left: 'prev,next,today',
                center: 'title',
                right: ''
            }
        };

        //retrieve the current season as number.
        this.adminService.currentSeason$.subscribe((data) => {
            this.currentSeason = data;
        });

        // retrieve selected admin season from DB
        this.adminService.adminDatas$
            .pipe(
                map(admins => admins.filter(admin => admin.adminKey === AdminKeys.SEASON))
            )
            .subscribe((data) => {
                this.adminService.historicDataNeedsUpdate$.next(+data[0].value);
            });
    }
}
