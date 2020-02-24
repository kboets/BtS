import {Component} from "@angular/core";
import {routerTransition} from "../../router.animations";

@Component({
    selector: 'app-league',
    templateUrl: './league.component.html',
    animations: [routerTransition()]
})
export class LeagueComponent {


}
