import {Component, Input} from "@angular/core";
import {routerTransition} from "../../../router.animations";

@Component({
    selector: 'bts-standing',
    templateUrl: './standing.component.html'
})
export class StandingComponent {

    @Input()
    showStanding : boolean;
}
