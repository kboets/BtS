import {Component} from "@angular/core";
import {routerTransition} from "../../router.animations";
import {GeneralError} from "../../general/generalError";
import {CountryService} from "./country.service";
import {catchError} from "rxjs/operators";
import {EMPTY} from "rxjs";

@Component({
    selector: 'bts-country',
    templateUrl: './country.component.html',
    animations: [routerTransition()]
})
export class CountryComponent {
    errorMessage: GeneralError;

    constructor(private countryService: CountryService) {
    }

    selectableCountries$ =  this.countryService.countries$
        .pipe(
            catchError(err => {
                this.errorMessage = err;
                return EMPTY;
            })
        );

    availableCountries$ = this.countryService.countries$
        .pipe(
            catchError(err => {
                this.errorMessage = err;
                return EMPTY;
            })
        );
}

