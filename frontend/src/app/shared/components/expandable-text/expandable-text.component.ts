import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  OnDestroy,
  signal,
  ViewChild,
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { EXPANDABLE_TEXT_LABELS } from '../../shared.constants';

@Component({
  selector: 'app-expandable-text',
  templateUrl: './expandable-text.component.html',
  styleUrls: ['./expandable-text.component.scss'],
  imports: [MatButton, MatIcon],
})
export class ExpandableTextComponent implements AfterViewInit, OnDestroy {
  protected readonly LABELS = EXPANDABLE_TEXT_LABELS;

  @Input() text = '';
  @Input() collapsedLines = 5;

  @ViewChild('textElement') textElement!: ElementRef<HTMLElement>;

  protected expanded = signal(false);
  protected overflowing = signal(false);

  private resizeObserver?: ResizeObserver;

  ngAfterViewInit(): void {
    this.resizeObserver = new ResizeObserver(() => this.checkOverflow());
    this.resizeObserver.observe(this.textElement.nativeElement);
  }

  ngOnDestroy(): void {
    this.resizeObserver?.disconnect();
  }

  toggle(): void {
    this.expanded.update((expanded) => !expanded);
  }

  private checkOverflow(): void {
    // Only measurable while clamped; keep the toggle visible once expanded
    if (this.expanded()) {
      return;
    }
    const element = this.textElement.nativeElement;
    this.overflowing.set(element.scrollHeight > element.clientHeight + 1);
  }
}
