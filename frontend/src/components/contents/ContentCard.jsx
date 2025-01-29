import React, { Component } from 'react';

class ContendCard extends Component {
    state = {}
    render() {
        return (
            <div className="card mt-2">
                <div className="card-body">
                    {this.props.children}
                </div>
            </div>
        );
    }
}

export default ContendCard;