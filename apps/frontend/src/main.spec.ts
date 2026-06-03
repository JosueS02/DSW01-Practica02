import * as platformBrowser from '@angular/platform-browser';

describe('main bootstrap', () => {
  it('should call bootstrapApplication', async () => {
    const spy = spyOn(platformBrowser, 'bootstrapApplication').and.returnValue(Promise.resolve({} as any));

    const { bootstrapApp } = await import('./main');
    await bootstrapApp();

    expect(spy).toHaveBeenCalled();
  });
});
