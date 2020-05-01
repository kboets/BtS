import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {PageHeaderModule} from "../../shared/modules";
import {LeagueComponent} from "./league.component";
import {LeagueRoutingModule} from "./league-routing.module";

@NgModule({
    imports: [CommonModule, LeagueRoutingModule, PageHeaderModule],
    declarations: [LeagueComponent]
})
export class LeagueModule{}
