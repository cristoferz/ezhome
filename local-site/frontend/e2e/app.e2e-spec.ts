import { Angular2TestsPage } from './app.po';

describe('angular2-tests App', function() {
  let page: Angular2TestsPage;

  beforeEach(() => {
    page = new Angular2TestsPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
