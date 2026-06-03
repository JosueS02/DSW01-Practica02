describe('Smoke test', () => {
  it('loads the Angular app root', () => {
    cy.visit('/');
    cy.get('app-root').should('exist');
  });
});
