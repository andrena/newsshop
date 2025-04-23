// Subscribe.spec.js
import { render, screen } from '@testing-library/vue'
import Subscribe from '@/subscribe/Subscribe.vue'

describe('Subscribe.vue', () => {

    test('renders correctly', () => {
        render(Subscribe)

        expect(screen.getByText('Newsletter Subscription')).toBeInTheDocument();
    })

})