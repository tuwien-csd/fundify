import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-impressum',
  imports: [],
  template: `
    <div class="m-5">
      <section>
        <h1>Impressum</h1>
        <h2>Web Publisher</h2>
        <address>
          <p>TU Wien</p>
          <p>Karlsplatz 13, 1040 Vienna</p>
          <p>Phone: <a href="tel:00431588010">+43 1 58801 0</a></p>
          <p>Fax: +43 1 5880141275</p>
        </address>
        <div class="mt-8">
          <h2>Copyright</h2>
          <p>
            The information on this server is provided by the FUNDify partners.
            <br />
            The partners independently create the funding information and enter
            it into the system. <br />
          </p>
          <p>© Copyright - unless stated otherwise – held by the partners.</p>
        </div>
      </section>
      <section class="mt-20">
        <h2>Disclaimer</h2>
        <p>
          All texts on the homepage have been carefully checked. However, we do
          not guarantee the accuracy, completeness and validity of the
          information. We thus accept no responsibility or liability. Links to
          other websites have been carefully selected. However, as we have no
          influence on the content of other websites, we do not assume
          responsibility for the information provided by other websites linked
          through this website.
        </p>
        <p>
          <strong>Contact</strong>: If you have any further questions about
          FUNDify, or want to share some feedback on our website, please contact
          us at
          <a href="mailto:ris-synergy@tuwien.ac.at"
            >ris-synergy&#64;tuwien.ac.at</a
          >
        </p>
      </section>
    </div>
  `,
  styleUrls: ['./impressum.component.scss'],
  styles: ``,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ImpressumComponent {}
