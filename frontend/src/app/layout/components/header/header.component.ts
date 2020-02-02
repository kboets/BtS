import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import {DateService} from "../../service/date.service";

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
    public pushRightClass: string;
    public currentDate: Date;

    constructor(private translate: TranslateService, public router: Router, private dateService: DateService) {

        this.router.events.subscribe(val => {
            if (
                val instanceof NavigationEnd &&
                window.innerWidth <= 992 &&
                this.isToggled()
            ) {
                this.toggleSidebar();
            }
        });
    }

    ngOnInit() {
        //console.log("Entering the ngOninit");
        this.pushRightClass = 'push-right';
        this.dateService.getCurrentTime().subscribe(
            (result:any) => this.createDate(result),
            (error: any) => console.log(error)
        );
        console.log("leaving the ngOninit on " +this.currentDate);

    }

    createDate(dateString: string) {
        let newDate = new Date(dateString);
        console.log(newDate);
        this.currentDate = newDate;
        console.log("leaving  on " +this.currentDate);
    }

    isToggled(): boolean {
        const dom: Element = document.querySelector('body');
        return dom.classList.contains(this.pushRightClass);
    }

    toggleSidebar() {
        const dom: any = document.querySelector('body');
        dom.classList.toggle(this.pushRightClass);
    }

    rltAndLtr() {
        const dom: any = document.querySelector('body');
        dom.classList.toggle('rtl');
    }

    onLoggedout() {
        localStorage.removeItem('isLoggedin');
    }

    changeLang(language: string) {
        this.translate.use(language);
    }
}
