import {Routes, RouterModule} from '@angular/router';
import {ModuleWithProviders} from '@angular/core';
import {DashboardDemoComponent} from './demo/view/dashboarddemo.component';
import {SampleDemoComponent} from './demo/view/sampledemo.component';
import {FormsDemoComponent} from './demo/view/formsdemo.component';
import {DataDemoComponent} from './demo/view/datademo.component';
import {PanelsDemoComponent} from './demo/view/panelsdemo.component';
import {OverlaysDemoComponent} from './demo/view/overlaysdemo.component';
import {MenusDemoComponent} from './demo/view/menusdemo.component';
import {MessagesDemoComponent} from './demo/view/messagesdemo.component';
import {MiscDemoComponent} from './demo/view/miscdemo.component';
import {EmptyDemoComponent} from './demo/view/emptydemo.component';
import {ChartsDemoComponent} from './demo/view/chartsdemo.component';
import {FileDemoComponent} from './demo/view/filedemo.component';
import {UtilsDemoComponent} from './demo/view/utilsdemo.component';
import {DocumentationComponent} from './demo/view/documentation.component';
import {LeagueComponent} from "./bts/league/league.component";
import {TeamsComponent} from "./bts/teams/teams.component";
import {ResultsComponent} from "./bts/results/results.component";

export const routes: Routes = [
    {path: '', component: DashboardDemoComponent},
    {path: 'leagues', component: LeagueComponent},
    {path: 'teams', component: TeamsComponent},
    {path: 'result', component: ResultsComponent},
    {path: 'components/sample', component: SampleDemoComponent},
    {path: 'components/forms', component: FormsDemoComponent},
    {path: 'components/data', component: DataDemoComponent},
    {path: 'components/panels', component: PanelsDemoComponent},
    {path: 'components/overlays', component: OverlaysDemoComponent},
    {path: 'components/menus', component: MenusDemoComponent},
    {path: 'components/messages', component: MessagesDemoComponent},
    {path: 'components/misc', component: MiscDemoComponent},
    {path: 'pages/empty', component: EmptyDemoComponent},
    {path: 'components/charts', component: ChartsDemoComponent},
    {path: 'components/file', component: FileDemoComponent},
    {path: 'components/utils', component: UtilsDemoComponent},
    {path: 'documentation', component: DocumentationComponent}
];

export const AppRoutes: ModuleWithProviders = RouterModule.forRoot(routes, {scrollPositionRestoration: 'enabled'});
