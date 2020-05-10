import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {PageHeaderModule} from "../../shared/modules";
import {TeamsRoutingModule} from "./teams-routing.module";
import {TeamsComponent} from "./teams.component";
import {StandingComponent} from "./standing/standing.component";

@NgModule({
    imports: [CommonModule, TeamsRoutingModule, PageHeaderModule],
    declarations: [TeamsComponent, StandingComponent]
})
export class TeamsModule{}
