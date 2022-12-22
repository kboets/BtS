import {Component, OnInit} from "@angular/core";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AlgorithmService} from "./algorithm.service";
import {EMPTY, Subject} from "rxjs";
import {Algorithm} from "../domain/algorithm";
import {GeneralError} from "../domain/generalError";
import {catchError, debounceTime} from "rxjs/operators";
import {ConfirmationService, MessageService} from "primeng/api";

@Component({
    selector: 'bts-algorithm',
    providers: [ConfirmationService, MessageService],
    templateUrl: './algorithm.component.html'
})
export class AlgorithmComponent implements OnInit {

    private errorMessageSubject = new Subject<GeneralError>();
    errorMessage$ = this.errorMessageSubject.asObservable();

    private allButCurrentAlgorithms: Algorithm[];
    private allAlgorithms: Algorithm[];
    public currentAlgorithm: Algorithm;
    public addAlgorithm: boolean;
    public viewOthers: boolean;
    public showDeleteMessage:boolean;
    newAlgorithm: Algorithm;
    submitted = false;
    addAlgorithmForm: FormGroup;
    confirmationMessage: string;
    isSavedOK: boolean;
    isEmptyAlgorithm: boolean;

    constructor(private fb: FormBuilder, private algorithmService: AlgorithmService, private confirmationService: ConfirmationService) {
        this.addAlgorithm = false;
        this.newAlgorithm = {} as Algorithm;
        this.confirmationMessage = 'Het algoritme werd opgeslagen en is nu de standaard';
        this.isSavedOK = false;
        this.isEmptyAlgorithm = true;
        this.allButCurrentAlgorithms = [];
        this.viewOthers = false;
        this.showDeleteMessage = false;

    }

    ngOnInit(): void {
        this.createAddForm();
        this.retrieveCurrentAlgorithm();
        this.retrieveAllButCurrentAlgorithm();
        this.retrieveAllAlgorithm();

        this.algorithmService.algorithmRefreshAction$
            .subscribe(()=> {
                this.retrieveCurrentAlgorithm();
                this.retrieveAllButCurrentAlgorithm();
                this.retrieveAllAlgorithm();
            });
    }

    private retrieveCurrentAlgorithm() {
        this.algorithmService.getCurrentAlgorithm()
            .pipe(catchError(err => {
                this.errorMessageSubject.next(err);
                return EMPTY;
            })
        ).subscribe((data) => {
            this.currentAlgorithm = data;
            if (data !== null) {
                this.isEmptyAlgorithm = false;
            }
        })
    }

    private retrieveAllButCurrentAlgorithm() {
        this.algorithmService.getAllButCurrentAlgorithms()
            .pipe(debounceTime(1000))
            .pipe(catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            ).subscribe((data) => {
                this.allButCurrentAlgorithms = data;
                if (this.allButCurrentAlgorithms.length === 0) {
                    this.viewOthers = false;
                }
            })
    }

    private retrieveAllAlgorithm() {
        this.algorithmService.getAlgorithms()
            .pipe(debounceTime(1000))
            .pipe(catchError(err => {
                    this.errorMessageSubject.next(err);
                    return EMPTY;
                })
            ).subscribe((data) => {
                this.allAlgorithms = data;
        })
    }

    private createAddForm() {
        this.addAlgorithmForm = this.fb.group({
            homeWin: 0,
            homeDraw: 0,
            homeLost: 0,
            homeBonus: 0,
            awayWin: 0,
            awayDraw: 0,
            awayLost: 0,
            awayMalus: 0,
            name: ['', Validators.required],
            current: {value: false},
            threshold: 0
        })
    }

    get addForm() { return this.addAlgorithmForm.controls; }

    onAddSubmit() {
        this.submitted = true;
        if (this.addAlgorithmForm.invalid) {
            return;
        }
        this.setAlgorithmValues();
        if (this.verifyAlreadyExisting()) {
            let dataError = new GeneralError();
            dataError.userFriendlyMessage = 'Dit algoritme bestaat reeds.'
            this.errorMessageSubject.next(dataError);
        } else {
            this.saveAlgorithm();
        }
    }

    private saveAlgorithm() {
        this.algorithmService.saveAlgorithm(this.newAlgorithm)
            .subscribe((data) => {
                this.removeAcknowledgeMessage();
                this.addAlgorithm = false;
                this.isSavedOK = true;
                this.isEmptyAlgorithm = false;
            })
    }

    private verifyAlreadyExisting(): boolean {
        let isSame = false;
        this.allAlgorithms.forEach(algorithm => {
            isSame = this.isSameAlgorithm(algorithm);
            if (isSame) {
                return true;
            }
        })
        return isSame;
    }

    onAddReset() {
        this.submitted = false;
        this.createAddForm();
    }

    openAddForm() {
        this.addAlgorithm = true;
    }

    undoAddForm() {
        this.addAlgorithm = false;
        this.viewOthers = false;
    }

    openOthers() {
        this.viewOthers = true;
    }

    setAlgorithmCurrent(algorithm: Algorithm) {
        algorithm.current = true;
        this.algorithmService.saveAlgorithm(algorithm)
            .subscribe((data) => {
                this.removeAcknowledgeMessage();
                this.addAlgorithm = false;
                this.isSavedOK = true;
                this.isEmptyAlgorithm = false;
            })
    }

    deleteAlgorithme(algorithm: Algorithm) {
        this.confirmationService.confirm({
            message: 'Verwijder dit algoritme ? Dit kan niet ongedaan gemaakt worden',
            header: 'Bevestiging',
            icon: 'pi pi-info-circle',
            accept: () => {
                this.algorithmService.deleteAlgorithm(algorithm)
                    .subscribe((data) => {
                        this.showDeleteMessage = true;
                        this.removeAcknowledgeMessage();
                    })
            }

        });
    }

    private setAlgorithmValues() {
        this.newAlgorithm = {} as Algorithm;
        this.newAlgorithm.homePoints = {
            'win' : this.addAlgorithmForm.get('homeWin').value,
            'draw' : this.addAlgorithmForm.get('homeDraw').value,
            'lose' : this.addAlgorithmForm.get('homeLost').value
        }
        this.newAlgorithm.awayPoints = {
            'win' : this.addAlgorithmForm.get('awayWin').value,
            'draw' : this.addAlgorithmForm.get('awayDraw').value,
            'lose' : this.addAlgorithmForm.get('awayLost').value
        }
        this.newAlgorithm.name = this.addAlgorithmForm.get('name').value;
        this.newAlgorithm.threshold = this.addAlgorithmForm.get('threshold').value;
        if (this.addAlgorithmForm.get('current').value === true) {
            this.newAlgorithm.current = true;
        } else {
            this.newAlgorithm.current = true;
        }
        this.newAlgorithm.type = 'WIN';
        this.newAlgorithm.homeBonus = this.addAlgorithmForm.get('homeBonus').value;
        this.newAlgorithm.awayMalus = this.addAlgorithmForm.get('awayMalus').value;

    }

    private isSameAlgorithm(algorithm: Algorithm) {
        return this.newAlgorithm.homeBonus === algorithm.homeBonus
            && this.newAlgorithm.awayMalus === algorithm.awayMalus
            && this.newAlgorithm.awayPoints.win === algorithm.awayPoints.win
            && this.newAlgorithm.awayPoints.lose === algorithm.awayPoints.lose
            && this.newAlgorithm.awayPoints.draw === algorithm.awayPoints.draw
            && this.newAlgorithm.homePoints.win === algorithm.homePoints.win
            && this.newAlgorithm.homePoints.lose === algorithm.homePoints.lose
            && this.newAlgorithm.homePoints.draw === algorithm.homePoints.draw;
    }


    private removeAcknowledgeMessage() {
        setTimeout(() => {
            this.isSavedOK = false;
            this.showDeleteMessage = false;
        }, 5000);
    }

}
