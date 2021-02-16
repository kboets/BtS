import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormGroup} from "@angular/forms";
import {SelectItem} from "primeng/api";

@Component({
    selector: 'bts-forecast',
    templateUrl: './forecast.component.html'
})
export class ForecastComponent implements OnInit {

    forecastForm: FormGroup;
    totalGames: SelectItem[];

    constructor(private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.totalGames = [];
        for (let i = 1; i < 11; i++) {
            this.totalGames.push({label: ''+i, value: i});
        }

        this.forecastForm = this.fb.group({
            allLeagues: {value: false, disabled: false},
            resultGames: ['notLost', []],
            selectedGames: []
        });
    }

}
