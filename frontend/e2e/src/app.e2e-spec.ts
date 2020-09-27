import {OmegaPage} from './app.po';

describe('Omega App', () => {
    let page: OmegaPage;

    beforeEach(() => {
        page = new OmegaPage();
    });

    it('should display welcome message', () => {
        page.navigateTo();
        expect(page.getTitleText()).toEqual('Welcome to Omega!');
    });
});
